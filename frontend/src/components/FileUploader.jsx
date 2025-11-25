import React, { useState } from 'react';
import { Upload, Button, message, Spin } from 'antd';
import { InboxOutlined } from '@ant-design/icons';
import { uploadAPI } from '../services/api';

const { Dragger } = Upload;

const FileUploader = () => {
  const [loading, setLoading] = useState(false);

  const props = {
    name: 'file',
    multiple: false,
    accept: '.csv',
    beforeUpload: (file) => {
      const isCSV = file.type === 'text/csv' || file.name.endsWith('.csv');
      if (!isCSV) {
        message.error(`${file.name} 不是有效的CSV文件!`);
      }
      return isCSV;
    },
    customRequest: ({ file, onSuccess, onError }) => {
      setLoading(true);
      const formData = new FormData();
      formData.append('file', file);

      uploadAPI.importGarminData(formData)
        .then(response => {
          setLoading(false);
          onSuccess(response);
          message.success('文件上传成功');
          console.log('上传成功:', response);
        })
        .catch(error => {
          setLoading(false);
          onError(error);
          message.error('文件上传失败，请重试');
          console.error('上传失败:', error);
        });
    },
    onChange(info) {
      const { status } = info.file;
      if (status === 'done') {
        message.success(`${info.file.name} 文件上传成功`);
      } else if (status === 'error') {
        message.error(`${info.file.name} 文件上传失败`);
      }
    },
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files);
    },
  };

  return (
    <div className="file-uploader">
      <Spin spinning={loading}>
        <Dragger {...props}>
          <p className="ant-upload-drag-icon">
            <InboxOutlined />
          </p>
          <p className="ant-upload-text">点击或拖拽CSV文件到此区域上传</p>
          <p className="ant-upload-hint">
            支持单个CSV文件上传，用于导入佳明运动数据
          </p>
        </Dragger>
      </Spin>
    </div>
  );
};

export default FileUploader;