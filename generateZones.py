#!/usr/bin/env python

import sys, itertools, os, random

def randomContacts(roomExclude, dr):
    r = []
    for (room, cs) in dr:
        if room!=roomExclude:
            r += [random.choice(cs)]
    return ",".join(r)
def main():
    content = sys.stdin.readlines()
    content = sorted(map(lambda x: x.split(), content))
    content = { x[0]: x[1] for x in content }
    print content
    rooms = itertools.groupby(content.keys(), lambda x: x[:-2])
    rooms = [(r, [c for c in cs]) for (r, cs) in rooms]
    rooms = filter(lambda x: len(x[1])>2, rooms)
    querySignerHost = rooms[0][1][0]
    querySignerRoom = rooms[0][0]


    for (room, computers) in rooms:
        for c in computers[:3]:
            f = open("conf/"+c, "wb")
            f.write("contacts:0>"+",".join(filter(lambda x: x!= c, computers))+"#1>"+randomContacts(room, rooms)+"\n")
            f.write("path:/uw/mimuw/"+room+"/"+c+"\n")
            f.write("querySigner:"+'/uw/mimuw/'+querySignerRoom+'/'+querySignerHost+','+querySignerHost+"\n")
            f.write("agentDelay:5000\n")
            f.close()
    f = open("selectedHosts", "wb")
    for (room, cs) in rooms:
        for c in cs[:3]:
            f.write("/uw/mimuw/"+room+"/"+c+","+content[c]+"\n")
    f.close()
if __name__ == '__main__':
    main()
