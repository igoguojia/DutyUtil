# mock server入口
import tools as tools
from mockApp import application
from wsgiref.simple_server import make_server


def ServerStart():
    httpd = make_server('localhost', 7784, application)
    print('mock Server开启监听:' + tools.GetIp() + ':7784')
    httpd.serve_forever()


if __name__ == "__main__":
    ServerStart()
