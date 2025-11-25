import React, { useEffect, useRef } from 'react';
import Chart from 'chart.js/auto';
import { Card, Typography } from 'antd';

const { Title, Text } = Typography;

const IntensityAnalysisChart = ({ data = null }) => {
  const chartRef = useRef(null);
  const chartInstance = useRef(null);

  // 模拟数据
  const mockData = {
    labels: ['低强度', '中强度', '高强度'],
    datasets: [
      {
        label: '时长 (分钟)',
        data: [120, 350, 180],
        backgroundColor: [
          'rgba(75, 192, 192, 0.7)',
          'rgba(54, 162, 235, 0.7)',
          'rgba(255, 99, 132, 0.7)'
        ],
        borderColor: [
          'rgba(75, 192, 192, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(255, 99, 132, 1)'
        ],
        borderWidth: 2
      }
    ]
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
        type: 'bar',
        data: chartData,
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            },
            tooltip: {
              callbacks: {
                label: function(context) {
                  return `${context.raw} 分钟`;
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
                  size: 14
                }
              }
            }
          },
          animation: {
            duration: 1000,
            easing: 'easeOutQuart'
          },
          barPercentage: 0.6
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
          <Title level={5} style={{ margin: 0 }}>运动强度分析</Title>
          <Text type="secondary">不同强度运动时长分布</Text>
        </div>
      }
      className="stat-card"
    >
      <div style={{ height: '250px', position: 'relative' }}>
        <canvas ref={chartRef}></canvas>
      </div>
      <div style={{ marginTop: 16, display: 'flex', justifyContent: 'space-around', paddingTop: 16, borderTop: '1px solid #f0f0f0' }}>
        <div style={{ textAlign: 'center' }}>
          <Text type="secondary">低强度</Text>
          <div style={{ marginTop: 4 }}>
            <Text strong style={{ fontSize: 18, color: 'rgba(75, 192, 192, 1)' }}>120</Text>
            <Text type="secondary" style={{ marginLeft: 4 }}>分钟</Text>
          </div>
        </div>
        <div style={{ textAlign: 'center' }}>
          <Text type="secondary">中强度</Text>
          <div style={{ marginTop: 4 }}>
            <Text strong style={{ fontSize: 18, color: 'rgba(54, 162, 235, 1)' }}>350</Text>
            <Text type="secondary" style={{ marginLeft: 4 }}>分钟</Text>
          </div>
        </div>
        <div style={{ textAlign: 'center' }}>
          <Text type="secondary">高强度</Text>
          <div style={{ marginTop: 4 }}>
            <Text strong style={{ fontSize: 18, color: 'rgba(255, 99, 132, 1)' }}>180</Text>
            <Text type="secondary" style={{ marginLeft: 4 }}>分钟</Text>
          </div>
        </div>
      </div>
    </Card>
  );
};

export default IntensityAnalysisChart;