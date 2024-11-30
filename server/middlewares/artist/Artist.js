
module.exports.create = async(req, res, next)=>{
    let {name, desc} = req.body
    if(!name || !desc)
    {
        return res.status(400).json({
            message: "Vui lòng cung cấp 'name' & 'desc'"
        })
    }
    let {birthDay, originName , artisName, duration} = desc
    if(!birthDay || !originName || !artisName || !duration)
    {
        return res.status(400).json({
            message: `Vui lòng cung cấp desc: ${!birthDay ? "'birthDay'":""} ${!originName ? "'originName'": ""} ${!artisName? "'artisName'":""} ${!duration? "'duration'": ""}`
        })
    }

    return next()
}