import axios from 'axios';

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
    return response.data;
  },
  error => {
    console.error('响应错误:', error);
    // 统一错误处理
    if (error.response) {
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
  getOverallStatistics: () => {
    return api.get('/statistics/overall');
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