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

TicketType.pre('save', function (next) {
    if (this.price === 0) {
        const typeTicketLower = this.typeTicket.toLowerCase().trim();
        switch (typeTicketLower) {
            case 'vip 1':
                this.price = 200000;
                break;
            case 'vip 2':
                this.price = 150000;
                break;
            case 'normal':
                this.price = 70000;
                break;
            default:
                this.price = 0; // Hoặc bạn có thể throw lỗi nếu typeTicket không hợp lệ
        }
    }
    next();
});

module.exports = mongoose.model('ticket_types', TicketType)