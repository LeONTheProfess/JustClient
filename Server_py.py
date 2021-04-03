import socket
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.connect(("8.8.8.8", 80))
print(f'Сервер запущен на {s.getsockname()}')
s.close()
sock = socket.socket()
sock.bind(('', 9090))
sock.listen(1)
while True:
    conn, addr = sock.accept()
    print("Подключился " + addr[0])
    try:
        while True:
            data = conn.recv(40960)
            s=data.decode()
            print(s)
            s=s.upper()
            if not data:
                break
            conn.send(str.encode(s))
        conn.close()
    except:
        print("Ошибка")
