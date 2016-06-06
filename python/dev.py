#!/usr/bin/python

#dev.py start|stop|restart

import os
import sys

os.chdir(os.path.dirname(os.path.realpath(__file__)))
os.system('python ../zz-rpc/python/gen/dev.py %s' % " ".join(sys.argv))

