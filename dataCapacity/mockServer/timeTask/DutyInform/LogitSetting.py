import logging
import os
from functools import wraps


def logit(logfile='default.log'):
    def logging_decorator(func):
        @wraps(func)
        def wrapped_function(*args, **kwargs):
            logging.basicConfig(level=logging.DEBUG,  # 设置日志输出格式
                                filename=os.path.split(os.path.realpath(__file__))[0] + '/' + logfile,  # log日志输出的文件位置和文件名
                                filemode="a",  # 文件的写入格式，w为重新写入文件，默认是追加
                                format="%(asctime)s - %(name)s - %(levelname)-9s - %(filename)-8s : %(lineno)s line - %(message)s",
                                # 日志输出的格式
                                # -8表示占位符，让输出左对齐，输出长度都为8位
                                datefmt="%Y-%m-%d %H:%M:%S"  # 时间输出的格式
                                )
            return func(*args, **kwargs)

        return wrapped_function

    return logging_decorator
