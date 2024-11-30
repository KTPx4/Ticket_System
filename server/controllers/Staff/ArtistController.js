const ArtistModel = require('../../models/ArtistModel')

module.exports.create = async(req, res)=>{
    try{
        let {name, desc} = req.body
        var art = await ArtistModel.create({
            name,
            desc
        })
        return res.status(200).json({
            status: "create artist success",
            message: "Tạo nghệ sĩ thành công",
            data: art
        })
    }
    catch(e)
    {
        console.log("Error at ArtistController - create: ", e)
        return res.status(400).json({
            status: "create artist failed",
            message: "Tạo nghệ sĩ thất bại",
            data: null
        })
    }

}