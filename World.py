from watchdog.events import FileSystemEvent

from constants import *

import json, os, nbtlib

class WorldFile:
    
    def __init__(self, e: FileSystemEvent):
        with open(e.src_path) as f:
            self.world_info = json.load(f) # latest_world.json
        
        self.data = []
        self.path = self.world_info['world_path']
        self.log_path = os.path.join(os.path.dirname(os.path.dirname(self.path)),'logs','latest.log')
        self.dat_path = os.path.join(self.path, 'level.dat')
        self.seed = self.get_nbt()['Data']['WorldGenSettings']['seed']
        print(self.get_player_data())
    
    
    def get_nbt(self):
        data = nbtlib.load(self.dat_path)
        return data

    def get_player_data(self):
        data = self.get_nbt().get('Data').get('Player')
        dim = data.get('Dimension')
        pos = Vec3(arr = data.get('Pos'))
        return Player(dim, pos)
        

class Vec3:
    def __init__(self, arr=None, x=None, y=None, z=None):
        self.x = x
        self.y = y
        self.z = z
        
        if arr:
            self.x = float(arr[0])
            self.y = float(arr[1])
            self.z = float(arr[2])
        
        

    def __str__(self) -> str:
        return f'X:{self.x} Y:{self.y} Z:{self.z}'
    
    
class Player:
    
    def __init__(self, dim: str, pos: Vec3):
        self.dim = dim.split(':')[1]
        self.pos = pos

    def __str__(self) -> str:
        return f'Dim: {self.dim} , Pos: {self.pos.__str__()}'