const mongoose = require('mongoose')
var root = process.env.IMG || 'localhost:3001/images'

const ArtistSchema = new mongoose.Schema({
    name: String,
    desc: {
        birthDay: String,
        originName: String,
        artisName: String,
        duration: String,
        more: {type: String, default: ""}
    },
    image: {
        type: String,
        get: v=> `${root}/${v}`
    },
    file:[
        {
            typeFile: {type: String, default: "image"},
            url: {type: String, default: null}
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