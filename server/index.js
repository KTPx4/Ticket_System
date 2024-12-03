const express = require('express')
const cors = require('cors')
const path = require('path')
require('dotenv').config()

// Call model
require('./models/NewsModel')
require('./models/EventModel')
require('./models/AccountModel')
require('./models/ArtistModel')
require('./models/StaffModel')

// Router
const StaffRouter = require('./routes/StaffRouter')
const ArtistRouter = require('./routes/ArtistRouter')
const EventRouter = require('./routes/EventRouter')
const AccountRouter = require('./routes/AccountRouter')
const StaffAuth = require("./middlewares/staffs/Staff");
const NewsRouter = require('./routes/NewsRouter')
const fs = require("fs");

// Import cron job
const ticketCronJob = require('./modules/CheckingTicket'); // Import cron job

// variable
const _APP = express()
const _PORT = process.env.PORT || 3003

//config
_APP.use(express.json())
_APP.use(express.urlencoded({extended: true}))
_APP.use(cors())
_APP.use('/public', express.static(path.join(__dirname, 'public')))
_APP.use((req, res, next)=>{
    req.vars = {root: __dirname}
    next()
})


// need account router - login - register

_APP.use('/api/v1/staff', StaffRouter(__dirname))
_APP.use('/api/v1/artist',  ArtistRouter(__dirname))
_APP.use('/api/v1/event', EventRouter(__dirname))
_APP.use('/api/v1/news', NewsRouter)
_APP.use('/api/v1/account', AccountRouter(__dirname))

_APP.use("/*",(req, res)=>{
    return res.status(404).json({
        status: "notfound",
        message: "Endpoint không hợp lệ"
    })
})

const createFolder = async()=>{
    var root = path.join(__dirname, "public")

    var listFolder = [
        `${root}/account`,
        `${root}/artist`,
        `${root}/event`,
    ]

    listFolder.forEach(path=>{
        if(!fs.existsSync(path))
        {
            fs.mkdir(path, (error) =>
            {
                if (error)
                {
                    console.log("Index.js - Error at create Folder: ", error.message);
                }
            });
        }
    })

}
const StartProgram = async()=>{
    await createFolder()

    await require('./models/DB')
        .then(async()=>{
            console.log("Connect DB Success");
            _APP.listen(_PORT, () => {
                console.log("App listen at: http://localhost:" + _PORT);
            })

            // Khởi chạy cron job
            ticketCronJob();

        })
        .catch((err)=>{
            console.log("Connect DB failed: ", err)
        })

}

StartProgram()