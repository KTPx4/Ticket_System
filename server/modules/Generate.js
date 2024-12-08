const fs = require('fs');
const path = require('path');
const EventModel = require('../models/EventModel')

module.exports.RandomChar = (length = 8) =>{
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * characters.length));
    }
    return result;
}

module.exports.AddImageEvent = async (root) => {
    try {
        // Lấy danh sách tất cả các sự kiện
        const events = await EventModel.find();

        // Đường dẫn đến thư mục public/event
        const eventDir = path.join(root, 'public', 'event');
        const defaultImage = path.join(eventDir, 'default.png'); // Đường dẫn ảnh mặc định

        // Kiểm tra xem ảnh mặc định có tồn tại không
        if (!fs.existsSync(defaultImage)) {
            throw new Error(`Default image not found at ${defaultImage}`);
        }

        for (const event of events) {
            const eventFolder = path.join(eventDir, event._id.toString()); // Tạo đường dẫn thư mục theo event id
            const eventImagePath = path.join(eventFolder, event.image || `${event._id.toString()}.png}`); // Đường dẫn ảnh của event

            // Tạo thư mục nếu chưa tồn tại
            if (!fs.existsSync(eventFolder)) {
                fs.mkdirSync(eventFolder, { recursive: true }); // Tạo thư mục nếu chưa có
            }

            // Kiểm tra xem ảnh của event có tồn tại trong thư mục không
            if (!event.image || !fs.existsSync(eventImagePath)) {
                // Nếu không tồn tại, sao chép ảnh mặc định vào thư mục của event
                const targetImagePath = path.join(eventFolder, event.image || `${event._id.toString()}.png}`); // Tên ảnh sẽ là `default.png`
                fs.copyFileSync(defaultImage, targetImagePath);

                console.log(`Copied default image to: ${targetImagePath}`);
            }
        }

        console.log('All missing images have been replaced with default images.');
    } catch (error) {
        console.error('Error in AddImageEvent:', error.message);
    }
};