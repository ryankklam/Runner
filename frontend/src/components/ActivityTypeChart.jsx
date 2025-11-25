import React, { useEffect, useRef } from 'react';
import Chart from 'chart.js/auto';
import { Card, Typography } from 'antd';

const { Title, Text } = Typography;

const ActivityTypeChart = ({ data = null }) => {
  const chartRef = useRef(null);
  const chartInstance = useRef(null);

  // 模拟数据，实际使用时将由父组件传入
  const mockData = {
    labels: ['跑步', '骑行', '游泳', '步行', '瑜伽'],
    datasets: [{
      data: [65, 15, 8, 7, 5],
      backgroundColor: [
        '#FF6384',
        '#36A2EB',
        '#FFCE56',
        '#4BC0C0',
        '#9966FF'
      ],
      borderWidth: 2,
      borderColor: '#fff'
    }]
  };

  useEffect(() => {
    // 使用传入的数据或模拟数据
    const chartData = data || mockData;
    
    // 销毁已存在的图表实例
    if (chartInstance.current) {
      chartInstance.current.destroy();
    }

    // 创建新图表
    if (chartRef.current) {
      const ctx = chartRef.current.getContext('2d');
      chartInstance.current = new Chart(ctx, {
        type: 'doughnut',
        data: chartData,
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'right',
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
                  const label = context.label || '';
                  const value = context.raw || 0;
                  const total = context.dataset.data.reduce((a, b) => a + b, 0);
                  const percentage = ((value / total) * 100).toFixed(1);
                  return `${label}: ${value} (${percentage}%)`;
                }
              }
            }
          },
          cutout: '50%',
          animation: {
            animateScale: true,
            animateRotate: true
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
  }, [data]);

  return (
    <Card
      title={
        <div>
          <Title level={5} style={{ margin: 0 }}>运动类型分布</Title>
          <Text type="secondary">各类运动活动占比统计</Text>
        </div>
      }
      className="stat-card"
    >
      <div style={{ height: '300px', position: 'relative' }}>
        <canvas ref={chartRef}></canvas>
      </div>
    </Card>
  );
};

export default ActivityTypeChart;