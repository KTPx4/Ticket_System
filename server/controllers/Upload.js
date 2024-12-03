const fs = require('fs').promises;
module.exports.uploadSingle = async (file, currentPath, id) => {
    try {
        if (file) {
            // Kiểm tra và tạo thư mục nếu không tồn tại
            await fs.mkdir(currentPath, { recursive: true });

            const name = file.originalname;
            const temp = name.split('.');
            const extension = temp[temp.length - 1];
            const newFilePath = `${currentPath}/${id}.${extension}`;

            // Di chuyển file đến thư mục mới
            await fs.rename(file.path, newFilePath);

            return `${id}.${extension}`;

        }
        return undefined;
    } catch (error) {
        console.error("Error in uploadSingle: ", error.message);
        throw error; // Throw error để xử lý lỗi ở nơi gọi hàm
    }
};