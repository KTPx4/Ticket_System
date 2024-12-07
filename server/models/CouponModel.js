const mongoose = require('mongoose')
const {Schema}= mongoose
const Generate = require('../modules/Generate')
const CouponSchema = new mongoose.Schema({
    code:{
      type: String,
      unique: true
    },
    name: String,
    desc: String,
    type: String,
    maxDiscount: {
        type: Number,
        default: -1
    },
    percentDiscount:{
        type: Number,
        default: 0
    },
    count:{
        type: Number,
        default: 10
    }
})
CouponSchema.pre('save', async function (next) {
    if (!this.code) {
        let isUnique = false;
        let newCode;

        // Lặp để tạo mã không trùng
        while (!isUnique) {
            newCode = Generate.RandomChar();
            const existingDoc = await mongoose.model('coupons').findOne({ code: newCode });
            if (!existingDoc) {
                isUnique = true;
            }
        }
        this.code = newCode; // Gán giá trị mã mới vào tài liệu
    }
    next();
});

module.exports = mongoose.model('coupons', CouponSchema)