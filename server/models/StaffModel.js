const mongoose = require('mongoose')
var root = process.env.IMG || 'localhost:3001/images'

const StaffSchema = new mongoose.Schema({
    name: String,
    user: { type: String, unique: true, required: true },
    pass: { type: String, required: true },
    phone: { type: String },
    email: { type: String, required: true},
    role: {type: String, default: "staff"},
    isDeleted: {type: Boolean, default: false},
    image: {
        type: String,
        // get: v=> `${root}/${v}`
    },
})
// Thêm hook để đặt giá trị mặc định cho các dòng đã có dữ liệu
StaffSchema.pre('save', function (next) {
    // Nếu giá trị của 'NameAvt' là null hoặc không được xác định, đặt giá trị mặc định là '_id.png'
    if (!this.image || this.image === null) {
        this.image = this._id + '.png';
    }
    next();
});
module.exports = mongoose.model('staffs', StaffSchema)