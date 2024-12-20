const mongoose = require('mongoose')
var root = process.env.IMG || 'localhost:3001/images'
const {Schema} = mongoose
const ArtistSchema = new mongoose.Schema({
    name: String,
    desc: {
        birthDay: String,
        originName: String,
        artistName: String,
        duration: String,
        more: {type: String, default: ""}
    },
    image: {
        type: String,
        default: " "
        // get: v=> `${root}/${v}`
    },
    followers:[
        {
            type: Schema.Types.ObjectId,
            ref: 'accounts'
        }
    ]
})

ArtistSchema.pre('save', function (next) {
    // Nếu giá trị của 'NameAvt' là null hoặc không được xác định, đặt giá trị mặc định là '_id.png'
    if (!this.image || this.image === null) {
        this.image = this._id + '.png';
    }
    next();
});
module.exports = mongoose.model('artists', ArtistSchema)