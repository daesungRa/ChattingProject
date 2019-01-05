# ChattingProject
> Source of chatting program written in java lang

## Summary

이 프로젝트는 **자바 멀티스레드** 와 **소켓**, **Swing** 으로 간단한 채팅 프로그램을 작성하는 것을 목표로 합니다.<br>
추가적으로 Multi-FileTranfer 기능도 구현합니다.<br/>

자바의 상속과 부품화, 인터페이스 구현, 오버라이드, 적절한 컬렉션 프레임워크 사용, 멀티스레드 및 소켓 프로그래밍 등의 기초개념을 학습하기 위한 프로젝트이므로 구조상 완결되지 않은 부분이 존재하니 양해 부탁드립니다.

## Used Tools

- JAVA 8
- UI: Swing
- Eclipse Photon

## Operational Flow

- 채팅 서버 구동 (ChattServer.java) > 클라이언트 실행 후 서버에 접속 (ChattClient.java)
- 접속한 클라이언트 수만큼 멀티스레드 환경 구축
- 리스트업된 유저 목록으로 귓속말 기능 구현
- 파일 전송 기능 구현

## How to use

![ChattServer](https://github.com/daesungRa/ChattingProject/blob/master/content/1)ChattServer.png)
![ChattServerOperating](https://github.com/daesungRa/ChattingProject/blob/master/content/2)ChattServerOperating.png)
![ChattServerMultiUser](https://github.com/daesungRa/ChattingProject/blob/master/content/3)ChattServerMultiUser.png)
![ChattServerWhisperOn](https://github.com/daesungRa/ChattingProject/blob/master/content/4)ChattServerWhisperOn.png)
![ChattClient](https://github.com/daesungRa/ChattingProject/blob/master/content/5)ChattClient.png)
![ChattClientLogout](https://github.com/daesungRa/ChattingProject/blob/master/content/6)ChattClientLogout.png)
![FileTransferPage](https://github.com/daesungRa/ChattingProject/blob/master/content/7)FileTransferPage.png)
![FileTransfer](https://github.com/daesungRa/ChattingProject/blob/master/content/8)FileTransfer.png)
![ReceiveFile](https://github.com/daesungRa/ChattingProject/blob/master/content/9)ReceiveFile.png)

## Fixes
- [181212] : 비정상적 접속종료 처리
- [181213] : 파일송수신 클래스 정의 및 FileDialog 적용, 송신할 파일 ADD 시 리스트에 표현되도록 작성
- [181214] : 사용하지 않는 변수 삭제, 버튼 동기화
- [181218] : 리스트업 된 여러 파일 송신 로직 작성
