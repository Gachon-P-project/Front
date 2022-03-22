# 가천인의 커뮤니티, 모여라가천 - MOGA!

![1-2_512](https://user-images.githubusercontent.com/53803882/104343381-37a82880-553f-11eb-8acd-2f874fa70995.png)

## [ Service ]

**✔︎ 현재 학기 시간표 자동 등록**
- 로그인하면 학생의 현재 학기 시간표를 자동으로 등록해줍니다.
- 현재 듣는 수업이 없는 경우 수업을 들었던 가장 최근 학기의 시간표를 등록합니다.

**✔︎ 시간표와 수업 게시판 연결**
- 시간표에서 수업을 선택하면 같은 수업을 듣는 학생들과 정보 공유를 할 수 있는 게시판으로 연결됩니다.

**✔︎ 수업 시작 알림기능**
- 시간표에 등록된 수업이 시작되기 전 수업 장소, 화상 강의 주소를 알림을 받아볼 수 있습니다.

**✔︎ 학과 공지사항, 알림기능**
- 학생의 학과를 자동으로 등록하여 손쉽게 학과 공지사항을 볼 수 있으며 새 글이 등록되면 알림을 받아볼 수 있습니다.

**✔︎ 학과 게시판**
- 본인의 학과에 해당하는 게시판을 이용 할 수 있습니다.

## [ Preview ]
**'MOGA'에서는 나와 같은 수업을 듣는 학생들과 정보 공유를 할 수 있는 소통의 장을 제공합니다.<br>
우리 과의 공지사항을 한눈에 보고, 공지사항 알림과 수업 시작 알림까지!**</br>
<br>
<img src='https://user-images.githubusercontent.com/53803882/104345341-91115700-5541-11eb-9a8f-026c791db8f5.png' width='200px'/>
<img src='https://user-images.githubusercontent.com/53803882/104345332-8eaefd00-5541-11eb-9a78-1d29dfc66b53.png' width='200px'/>
<img src='https://user-images.githubusercontent.com/53803882/104344730-e26d1680-5540-11eb-9211-56b26fc2b46f.png' width='200px'/>
<img src='https://user-images.githubusercontent.com/53803882/104344826-fca6f480-5540-11eb-83ca-d73b136f1017.png' width='200px'/>
<img src='https://user-images.githubusercontent.com/53803882/104344879-0892b680-5541-11eb-8f02-1a098c25ebc0.png' width='200px'/>
<img src='https://user-images.githubusercontent.com/53803882/104344867-0597c600-5541-11eb-931f-03eeae6b9e0a.png' width='200px'/>
<img src='https://user-images.githubusercontent.com/53803882/104344871-06c8f300-5541-11eb-8975-74a21c763af9.png' width='200px'/>
<img src='https://user-images.githubusercontent.com/53803882/104344874-07fa2000-5541-11eb-9f49-2654c533aa93.png' width='200px'/>

## [ System Architecture ]
<img width="1956" alt="iShot2021-02-22 00 33 03" src="https://user-images.githubusercontent.com/53803882/108629953-b03ac700-74a5-11eb-9fc1-bd2b524627b1.png">

## [ Develop Environment ]
#### FrontEnd
- Web : React.js
- Android : Java, Kotlin

#### Server
- API Server : Node.js(express)
- Hosting : AWS EC2
- Database : AWS RDS(Maria)
- Container : Docker
- CI/CD : Jenkins
- Reverse Proxy : NginX
