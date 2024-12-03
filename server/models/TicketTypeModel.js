const mongoose = require('mongoose')

const {Schema} = mongoose
const TicketType = new mongoose.Schema({
    event:{
        type: Schema.Types.ObjectId,
        ref:"events"
    },
    typeTicket:{type: String, default:"Vip 1"}, // Vip 1, Vip 2, Normal
    location: {type: String, default: "A"}, // A, B, C
    price: {type: Number, default: 0},
})
module.exports = mongoose.model('ticket_types', TicketType)