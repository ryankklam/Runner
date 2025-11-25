import React, { useEffect, useRef } from 'react';
import Chart from 'chart.js/auto';
import { Card, Typography, Select } from 'antd';

const { Title, Text } = Typography;
const { Option } = Select;

const ActivityTrendChart = ({ data = null, timeRange = 'week' }) => {
  const chartRef = useRef(null);
  const chartInstance = useRef(null);
  const [selectedMetric, setSelectedMetric] = React.useState('distance');

  // 模拟数据 - 根据时间范围生成不同的数据
  const generateMockData = () => {
    let labels, distanceData, durationData, caloriesData;
    
    switch (timeRange) {
      case 'week':
        labels = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
        distanceData = [4.5, 3.2, 5.8, 0, 6.1, 8.5, 7.2];
        durationData = [32, 24, 40, 0, 45, 60, 50];
        caloriesData = [320, 230, 410, 0, 430, 590, 510];
        break;
      case 'month':
        labels = ['第1周', '第2周', '第3周', '第4周'];
        distanceData = [22.3, 28.7, 25.1, 32.5];
        durationData = [190, 220, 185, 240];
        caloriesData = [1950, 2350, 1890, 2450];
        break;
      case 'year':
        labels = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'];
        distanceData = [78, 92, 110, 130, 145, 160, 175, 162, 150, 130, 100, 85];
        durationData = [650, 780, 920, 1080, 1200, 1350, 1480, 1350, 1250, 1080, 850, 720];
        caloriesData = [6800, 8200, 9800, 11500, 12800, 14200, 15600, 14300, 13200, 11500, 9200, 7800];
        break;
      default:
        labels = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
        distanceData = [4.5, 3.2, 5.8, 0, 6.1, 8.5, 7.2];
        durationData = [32, 24, 40, 0, 45, 60, 50];
        caloriesData = [320, 230, 410, 0, 430, 590, 510];
    }

    return {
      labels,
      datasets: [
        {
          label: '距离 (公里)',
          data: distanceData,
          borderColor: '#FF6384',
          backgroundColor: 'rgba(255, 99, 132, 0.1)',
          tension: 0.3,
          fill: true
        },
        {
          label: '时长 (分钟)',
          data: durationData,
          borderColor: '#36A2EB',
          backgroundColor: 'rgba(54, 162, 235, 0.1)',
          tension: 0.3,
          fill: true
        },
        {
          label: '卡路里 (千卡)',
          data: caloriesData,
          borderColor: '#FFCE56',
          backgroundColor: 'rgba(255, 206, 86, 0.1)',
          tension: 0.3,
          fill: true
        }
      ]
    };
  };

  useEffect(() => {
    // 使用传入的数据或生成模拟数据
    const chartData = data || generateMockData();
    
    // 销毁已存在的图表实例
    if (chartInstance.current) {
      chartInstance.current.destroy();
    }

    // 创建新图表
    if (chartRef.current) {
      const ctx = chartRef.current.getContext('2d');
      chartInstance.current = new Chart(ctx, {
        type: 'line',
        data: chartData,
        options: {
          responsive: true,
          maintainAspectRatio: false,
          interaction: {
            mode: 'index',
            intersect: false,
          },
          plugins: {
            legend: {
              position: 'top',
              labels: {
                font: {
                  size: 14
                },
                padding: 20
              }
            },
            tooltip: {
              callbacks: {
                label: function(context) {
                  let label = context.dataset.label || '';
                  if (label) {
                    label += ': ';
                  }
                  label += context.raw;
                  return label;
                }
              }
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              grid: {
                color: 'rgba(0, 0, 0, 0.05)'
              },
              ticks: {
                font: {
                  size: 12
                }
              }
            },
            x: {
              grid: {
                display: false
              },
              ticks: {
                font: {
                  size: 12
                }
              }
            }
          },
          animation: {
            duration: 1000,
            easing: 'easeOutQuart'
          }
        }
      });
    }

    // 组件卸载时销毁图表
    return () => {
      if (chartInstance.current) {
        chartInstance.current.destroy();
      }
    };
  }, [data, timeRange, selectedMetric]);

  const handleMetricChange = (value) => {
    setSelectedMetric(value);
  };

  return (
    <Card
      title={
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <Title level={5} style={{ margin: 0 }}>运动趋势</Title>
            <Text type="secondary">{timeRange === 'week' ? '本周' : timeRange === 'month' ? '本月' : '本年'}运动数据变化</Text>
          </div>
          <Select 
            value={selectedMetric} 
            onChange={handleMetricChange}
            style={{ width: 120 }}
          >
            <Option value="distance">距离</Option>
            <Option value="duration">时长</Option>
            <Option value="calories">卡路里</Option>
          </Select>
        </div>
      }
      className="stat-card"
    >
      <div style={{ height: '350px', position: 'relative' }}>
        <canvas ref={chartRef}></canvas>
      </div>
    </Card>
  );
};

export default ActivityTrendChart;