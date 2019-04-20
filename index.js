const express = require('express');
const mysql = require('promise-mysql');
const moment = require('moment');
const bodyParser = require('body-parser');

const multer = require('multer');     //파일 업로드하기 위한 모듈
const fs = require('fs');             //파일 시스템
const cors = require('cors');         //다중 서버로 접속하게 해주는 기능을 제공, 다른 ip 로 다른서버에 접속
const app = express();

var _storage = multer.diskStorage({
    limits: {
          fieldSize: 1024 * 1024
    },
    destination: function (req, file, cb) {
      cb(null, 'uploads/')
    },
    filename: function (req, file, cb) {
      cb(null, file.originalname+'.jpg');
    }
  })
var upload = multer({ storage: _storage })

app.use(bodyParser.urlencoded({extended:true}));
app.use(bodyParser.json());
app.use((req, res, next) => {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Methods", "POST, GET, PUT");
    res.header("Access-Control-Allow-Headers", 'Authorization, CONTENT-TYPE');
    res.header('Cache-Control', 'private, no-cache, no-store, must-revalidate');
    res.header('Expires', '-1');
    res.header('Pragma', 'no-cache');
    next();
});


/////////////////////////////////////////API////////////////////////////////////////

    mysql.createConnection({
        host: 'localhost',
        user: 'root',
        password: 'p12481632',
        database: 'system_project_db'
    }).then((conn) => {
        
        //이미지 전송
        app.post('/upimage', upload.any(), (req, res)=>{
            console.log('who get in here post /users');
            console.log(req.files);
            res.send('Uploaded : '+req.files[0].filename);
        });
        //게시글 택스트 저장
        app.post('/uptext', upload.any(),  (req, res) => {
            var inputData= req.body;
            console.log(req.body);
            const title = req.body.title;
            const text = req.body.text;
            const uesr_id = req.body.user_id;
            const picture_id = req.body.picture_id;
            let query = `INSERT INTO POST (title,text,uesr_id,picture_id) VALUES
             (${mysql.escape(title)}, ${ mysql.escape(text) }, ${ mysql.escape(uesr_id) }, ${ mysql.escape(picture_id) })`;

            conn.query(query).then(rows => {
                res.send('success')
            }).catch(err => {
                res.send('fail');
            });
        });

        //유저 num_id 를 통해 유저 정보 반환
        //username?user_num=:user_num
        app.get('/username', (req, res) => {
            let query =
                `SELECT * FROM user where user.user_num = ${ req.query.user_num }`;
            conn.query(query).then(rows => {
                res.send({
                    status: 'success',
                    result: rows
                })
                console.log("GET OK!!!");
            }).catch(err => {
                res.send('fail');
            });
        });
        //refreshpost?no=:no 항상 마지막 게시물 +1 을 한다 
        app.get('/refreshpost', (req, res) => {
                let query = `SELECT * FROM post where no = ${req.query.no}+1`;
                conn.query(query).then(rows => {
                    res.send( rows);
                }).catch(err => {
                    res.send('fail');
                });
        });
        app.get('/image/:name', (req,res)=>{     
            var filename = req.params.name;
            console.log(__dirname+'/uploads/'+filename+'.jpg');
            fs.exists(__dirname+'/uploads/'+filename+'.jpg',  (exists)=> {
                if (exists) {
                    fs.readFile(__dirname+'/uploads/'+filename+'.jpg', (err,data)=>{
                        res.writeHead(200, { "Context-Type": "image/jpg" });//보낼 헤더를 만
                        res.write(data);   //본문을 만들고
                        res.end();
                    });
                } else {
                    res.end('file is not exists');
                }
            })
        });
        //모든 유저 정보 반환(비밀번호 제외)
        app.get('/user-info', (req, res) => {
            let query = '';
            if (req.query.limit === '0') {
                query = `SELECT user_num,user_id,name FROM user`;
            } else {
                query = `SELECT user_num,user_id,name FROM user LIMIT ${req.query.limit}`;
            }
            conn.query(query).then(rows => {
                res.send({
                    status: 'success',
                    result: rows.uesr_id
                })
            }).catch(err => {
                res.send('fail');
            });
        });
        //post test
         app.post('/post', (req, res) => {
            console.log('who get in here post /users');
            var inputData= req.body;
            console.log(req.body);
            let query = req.body;

            conn.query(query).then(rows => {
                res.send({
                    status: 'success',
                    result: req.body
                })
            }).catch(err => {
                res.send('fail');
            });
        });
        //회원 가입(아이디 , 이름 , 비밀번호) 
        app.post('/sign-up', (req, res) => {
            var inputData= req.body;

            if(!req.body.user_id||!req.body.name||!req.body.password){
                return res.json({result: 'fail', msg: 'no user info'});
            }else{
                const user_id = req.body.user_id;
                const name = req.body.name;
                const password = req.body.password;
                let q = `SELECT * FROM user WHERE user_id = ${mysql.escape(user_id)}`;
                conn.query(q).then(rows => {
                    if (rows.length > 0) {
                      res.json({ result: 'fail', msg: 'email is already exist' });
                    } else {
                      let q = `INSERT INTO user (user_id,name,password) VALUES (${mysql.escape(user_id)}, ${ mysql.escape(name) }, ${ mysql.escape(password) })`;
                      conn.query(q)
                        .catch(err => console.log('occur error in /sign-in || ' + err.toString()))
                        .then(result => res.json({ result: 'success', id: result.insertId }));
                    }
                });
            }
        });

        //로그인
        app.get('/log-in', (req, res) => {
            let q = `SELECT * FROM user WHERE user_id = ${mysql.escape(req.query.user_id)}`;
            conn.query(q).then(rows => {
              if (rows.length > 0) {
                let q = `SELECT * FROM user WHERE user_id = ${mysql.escape(req.query.user_id)} AND password = ${mysql.escape(req.query.password)}`;
                conn.query(q).then(rows => {
                  if (rows.length > 0) {
                    res.json({ result: 'success', data: rows[0] });
                  } else {
                    res.send("fail");
                  }
                });
              } else {
                res.send("fail");
              }
            });
          });
    });

app.set('port', 3000);
app.listen(app.get('port'), () => console.log("Conneted " + app.get('port') + " port"));