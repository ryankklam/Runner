import React, { useEffect, useRef } from 'react';
import { Card } from 'antd';
import Chart from 'chart.js/auto';

const ChartComponent = ({ 
  type = 'bar', 
  data, 
  options, 
  title,
  loading = false
}) => {
  const chartRef = useRef(null);
  const chartInstance = useRef(null);

  useEffect(() => {
    if (!chartRef.current || loading) return;

    // 销毁旧的图表实例
    if (chartInstance.current) {
      chartInstance.current.destroy();
    }

    // 创建新的图表实例
    const ctx = chartRef.current.getContext('2d');
    chartInstance.current = new Chart(ctx, {
      type,
      data,
      options: {
        responsive: true,
        maintainAspectRatio: false,
        ...options
      }
    });

    // 清理函数
    return () => {
      if (chartInstance.current) {
        chartInstance.current.destroy();
      }
    };
  }, [type, data, options, loading]);

  return (
    <Card 
      title={title}
      className="chart-container"
      loading={loading}
      style={{ height: '100%', display: 'flex', flexDirection: 'column' }}
    >
      <div style={{ flex: 1, position: 'relative', height: '300px' }}>
        <canvas ref={chartRef} />
      </div>
    </Card>
  );
};

// 预设常用图表配置
export const chartUtils = {
  // 柱状图默认配置
  getBarChartOptions: (title = '') => ({
    plugins: {
      title: {
        display: !!title,
        text: title,
        font: {
          size: 16
        }
      },
      legend: {
        display: true,
        position: 'top'
      }
    },
    scales: {
      y: {
        beginAtZero: true
      }
    }
  }),
  
  // 折线图默认配置
  getLineChartOptions: (title = '') => ({
    plugins: {
      title: {
        display: !!title,
        text: title,
        font: {
          size: 16
        }
      },
      legend: {
        display: true,
        position: 'top'
      }
    },
    scales: {
      y: {
        beginAtZero: true
      }
    }
  }),
  
  // 饼图默认配置
  getPieChartOptions: (title = '') => ({
    plugins: {
      title: {
        display: !!title,
        text: title,
        font: {
          size: 16
        }
      },
      legend: {
        display: true,
        position: 'right'
      }
    }
  })
};

export default ChartComponent;