from watchdog.events import FileSystemEvent, FileSystemEventHandler
from watchdog.observers import Observer
import json, os, time, glob

from World import WorldFile

with open(os.path.join(os.getcwd(), 'data', 'settings.json')) as f:
    SETTINGS = json.load(f)

class FileHandler(FileSystemEventHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.tracked_paths = []

    
    def on_modified(self, event: FileSystemEvent) -> None:
        if not event.src_path.endswith('latest_world.json'):
            return
        
        with open(event.src_path) as f:
            data = json.load(f)
        
        if data['world_path'] in self.tracked_paths:
            return
            
        self.tracked_paths.append(event.src_path)
        w = WorldFile(event)

            
if __name__ == '__main__':

    srigt_path = os.path.join(os.path.expanduser("~"),'speedrunigt')
    
    obs = Observer()
    evt_handler = FileHandler()
    obs.schedule(evt_handler, srigt_path)
    obs.start()
    
    print('Tracking')
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        obs.stop()
        exit(0)
