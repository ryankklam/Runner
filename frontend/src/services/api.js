import axios from 'axios';

// 打印环境变量信息用于调试
console.log('环境变量 VITE_API_BASE_URL:', import.meta.env.VITE_API_BASE_URL);

// 创建axios实例
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 可以在这里添加token等认证信息
    return config;
  },
  error => {
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  response => {
    console.log('API请求成功:', response.config.baseURL + response.config.url);
    console.log('响应数据:', response.data);
    return response.data;
  },
  error => {
    console.error('响应错误:', error);
    // 详细的错误信息
    if (error.response) {
      console.error('错误状态:', error.response.status);
      console.error('错误数据:', error.response.data);
      console.error('请求URL:', error.config.baseURL + error.config.url);
      switch (error.response.status) {
        case 401:
          console.error('未授权，请重新登录');
          break;
        case 403:
          console.error('拒绝访问');
          break;
        case 404:
          console.error('请求地址不存在');
          break;
        case 500:
          console.error('服务器错误');
          break;
        default:
          console.error('请求失败:', error.response.data?.message || '未知错误');
      }
    } else if (error.request) {
      console.error('网络错误 - 无法连接到服务器:', error.request);
      console.error('请求URL:', error.config?.baseURL + error.config?.url);
      console.error('基础URL:', api.defaults.baseURL);
      console.error('请求配置:', error.config);
    } else {
      console.error('请求配置错误:', error.message);
    }
    return Promise.reject(error);
  }
);

// 文件上传API
export const uploadAPI = {
  // 导入佳明数据
  importGarminData: (formData) => {
    return api.post('/import/garmin', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },
  // 获取导入记录
  getImportRecords: (params) => {
    return api.get('/import/records', { params });
  }
};

// 统计分析API
export const statisticsAPI = {
  // 获取总体统计
  getOverallStatistics: async () => {
    try {
      const endpoint = '/statistics/overall';
      const fullUrl = `${api.defaults.baseURL}${endpoint}`;
      console.log('准备发送请求到:', fullUrl);
      console.log('基础URL:', api.defaults.baseURL);
      console.log('环境变量:', import.meta.env.VITE_API_BASE_URL);
      
      // 直接使用 axios 而不是包装后的 api 实例进行测试
      const response = await axios.get(fullUrl, {
        timeout: 5000,
        headers: {
          'Content-Type': 'application/json'
        }
      });
      
      console.log('直接请求成功，状态码:', response.status);
      console.log('响应数据:', response.data);
      return response.data;
    } catch (error) {
      console.error('直接请求失败:');
      if (error.response) {
        console.error('HTTP状态:', error.response.status);
        console.error('响应数据:', error.response.data);
      } else if (error.request) {
        console.error('请求已发送但无响应');
        console.error('错误码:', error.code);
      } else {
        console.error('请求配置错误:', error.message);
      }
      throw error;
    }
  },
  // 按时间范围获取统计
  getStatisticsByDateRange: (startDate, endDate) => {
    return api.get('/statistics/date-range', {
      params: { startDate, endDate }
    });
  },
  // 按活动类型分组统计
  getStatisticsByActivityType: () => {
    return api.get('/statistics/by-type');
  },
  // 获取最近活动
  getRecentActivities: (limit = 10) => {
    return api.get('/statistics/recent-activities', {
      params: { limit }
    });
  },
  // 获取月度趋势
  getActivityTrendByMonth: (months = 6) => {
    return api.get('/statistics/trend/monthly', {
      params: { months }
    });
  },
  // 获取心率区间统计
  getHeartRateZoneStatistics: () => {
    return api.get('/statistics/heart-rate-zones');
  },
  // 获取配速区间统计
  getPaceZoneStatistics: () => {
    return api.get('/statistics/pace-zones');
  }
};

export default api;