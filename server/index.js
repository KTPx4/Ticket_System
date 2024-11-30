const express = require('express')
const cors = require('cors')
const path = require('path')
require('dotenv').config()

// Router
const StaffRouter = require('./routes/StaffRouter')
const ArtistRouter = require('./routes/ArtistRouter')
// variable
const _APP = express()
const _PORT = process.env.PORT || 3003

//config
_APP.use(express.json())
_APP.use(express.urlencoded({extended: true}))
_APP.use(cors())
_APP.use('/images', express.static(path.join(__dirname, 'public')))
_APP.use((req, res, next)=>{
    req.vars = {root: __dirname}
    next()
})



_APP.use('/api/v1/staff', StaffRouter(__dirname))
_APP.use('/api/v1/artist', ArtistRouter(__dirname))

const StartProgram = async()=>{
    await require('./models/DB')
        .then(async()=>{
            console.log("Connect DB Success");
            _APP.listen(_PORT, () => {
                console.log("App listen at: http://localhost:" + _PORT);
            })
        })
        .catch((err)=>{
            console.log("Connect DB failed: ", err)
        })

}
StartProgram()