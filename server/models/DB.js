const mongoose = require('mongoose')

// const serverDB = process.env.SERVER_DB || "mongodb://127.0.0.1:27017"
// const dbName = process.env.DBNAME || "Quizlet"

// const uri = `${serverDB}/${dbName}`

const conString = process.env.DBCON || "mongodb://127.0.0.1:27017/TicketBooking"


module.exports = mongoose.connect(conString)