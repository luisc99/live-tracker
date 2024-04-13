from watchdog.events import FileSystemEvent, FileSystemEventHandler
from watchdog.observers import Observer
import json, os, time, glob

from WorldFile import WorldFile

with open(os.path.join(os.getcwd(), 'data', 'settings.json')) as f:
    SETTINGS = json.load(f)

class FileHandler(FileSystemEventHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.last_event_time = 0
        self.last_event_path = ""
        self.tracked_paths = []

    def on_created(self, event: FileSystemEvent) -> None:
        if event.src_path in self.tracked_paths:
            return
        
        self.tracked_paths.append(event.src_path)
        
        print(event.src_path)
        
        
def watch(dir):
    observer = Observer()
    event_handler = FileHandler()
    observer.schedule(event_handler, dir, recursive=False)
    observer.start()

if __name__ == '__main__':
    instances_dir = os.path.join(SETTINGS['mmc_path'], 'instances')
    saves_dirs = glob.glob(os.path.join(instances_dir, '*', '.minecraft', 'saves'))
    tracked = []
    for s in saves_dirs:
        if s not in tracked:
            watch(s)
            tracked.append(s)

    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        exit(0)
