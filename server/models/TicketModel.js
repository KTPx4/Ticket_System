const mongoose = require('mongoose')

const {Schema} = mongoose
// Thư viện để định dạng ngày tháng
const moment = require('moment');

const TicketSchema = new mongoose.Schema({
    event:{
        type: Schema.Types.ObjectId,
        ref: "events"
    },
    name:{type: String},
    position: Number, // 1 -> 60
    desc: String,
    info: {
      type: Schema.Types.ObjectId,
      ref: 'ticket_types'
    },
    isAvailable: {type: Boolean, default: true},
    accBuy: {
        type: Schema.Types.ObjectId,
        ref: "accounts",
        default: null
    },
    expiresAt: { type: Date, default: null } // Thời gian hết hạn

})
// Middleware tự động tạo giá trị cho `name`
TicketSchema.pre('save', async function (next) {
    if (!this.name && this.event) {
        const EventModel = mongoose.model('events'); // Model `events`
        const event = await EventModel.findById(this.event); // Lấy dữ liệu event
        if (event) {
            this.name = `Vé ${event.name}`; // Thiết lập giá trị mặc định
        }

        // Middleware tự động cập nhật `expiresAt` khi `isAvailable` = false
        if (this.isAvailable === false && this.accBuy === null) {
            this.expiresAt = new Date(Date.now() + 10 * 60 * 1000); // Sau 10 phút
        } else {
            this.expiresAt = null; // Reset nếu không cần TTL
        }
    }
    next();
});
module.exports = mongoose.model('tickets', TicketSchema)