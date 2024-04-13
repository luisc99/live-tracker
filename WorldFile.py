from watchdog.events import FileSystemEvent

from constants import *

class WorldFile:
    
    def __init__(self, event: FileSystemEvent):
        self.data = []