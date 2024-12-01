const ArtistModel = require('../../models/ArtistModel')
const mongoose = require('mongoose');

module.exports.create = async(req, res, next)=>{
    let {name, desc} = req.body
    if(!name || !desc)
    {
        return res.status(400).json({
            message: "Vui lòng cung cấp 'name' & 'desc'"
        })
    }
    let {birthDay, originName , artistName, duration} = desc
    if(!birthDay || !originName || !artistName || !duration)
    {
        return res.status(400).json({
            message: `Vui lòng cung cấp desc: ${!birthDay ? "'birthDay'":""} ${!originName ? "'originName'": ""} ${!artistName? "'artistName'":""} ${!duration? "'duration'": ""}`
        })
    }

    return next()
}

module.exports.isExistID = async(req, res, next)=>{
    let {id} = req.params
    if(!id)
    {
        return res.status(400).json({
            message: "Thiếu id nghệ sĩ"
        })
    }
    // Kiểm tra nếu id không phải là ObjectId hợp lệ
    if (!mongoose.Types.ObjectId.isValid(id)) {
        return res.status(400).json({
            message: "Id không hợp lệ!"
        });
    }

    var art = await ArtistModel.findOne({_id: id})
    if(!art)
    {
        return res.status(400).json({
            message: "Nghệ sĩ không tồn tại"
        })
    }
    req.vars.Artist = art
    return next()
}