import jwt
import time
from jwt import exceptions

algorithm = 'HS256'
SECRET = 'jnulzk'
keeptime = 25200
headers = {
    'alg': algorithm,
    'type': 'jwt'
}
# exp = int(time.time() + keeptime)  # token 超时时间为7小时


def create_token(userID, authStr):
    payload = {
        'userID': userID,
        'exp': int(time.time() + keeptime),
        'authID': authStr
    }
    token = jwt.encode(payload=payload, key=SECRET, algorithm=algorithm, headers=headers)
    return token


def validate_token(token):
    """校验token的函数，校验通过则返回解码信息

    """
    payload = None
    msg = None
    try:
        payload = jwt.decode(token, SECRET, algorithms=[algorithm])
        # jwt有效、合法性校验
    except exceptions.ExpiredSignatureError:
        msg = 'token已失效'
    except jwt.DecodeError:
        msg = 'token认证失败'
    except jwt.InvalidTokenError:
        msg = '非法的token'
    return payload, msg


if __name__ == "__main__":
    token = create_token('111', 'aaa')
    time.sleep(2)

    payload, msg = validate_token(token)
    print(payload, msg)
