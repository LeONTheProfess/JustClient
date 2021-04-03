import socket

sock = socket.socket()
sock.connect(('127.0.0.1', 9090))
sock.send(str.encode("hello world"))

data = sock.recv(1024)
sock.close()

print (data.decode())
