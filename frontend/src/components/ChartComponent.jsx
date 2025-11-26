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
      style={{ 
        height: '100%', 
        display: 'flex', 
        flexDirection: 'column',
        borderRadius: 'var(--border-radius-sm)',
        boxShadow: 'var(--box-shadow-light)',
        border: '1px solid var(--border-color)',
        transition: 'all 0.2s ease',
        backgroundColor: '#FFFFFF'
      }}
      headStyle={{ 
        borderBottom: '1px solid var(--border-color)',
        padding: '14px 16px',
        fontSize: '16px',
        fontWeight: '600',
        backgroundColor: '#FFFFFF'
      }}
      bodyStyle={{ 
        flex: 1, 
        padding: '16px',
        margin: 0,
        backgroundColor: '#FFFFFF'
      }}
      hoverable
    >
      <div style={{ flex: 1, position: 'relative', height: '300px' }}>
        <canvas ref={chartRef} />
      </div>
    </Card>
  );
};

// 极简现代风格图表颜色主题
export const miChartColors = {
  primary: '#FF6A00',
  secondary: '#FF8C40',
  success: '#00B42A',
  warning: '#FF7D00',
  error: '#F53F3F',
  info: '#1890FF',
  light: '#F0F0F0',
  dark: '#222222',
  // 渐变色
  gradient: {
    primary: {
      from: '#FF6A00',
      to: '#FF8C40'
    },
    success: {
      from: '#00B42A',
      to: '#52C41A'
    },
    info: {
      from: '#1890FF',
      to: '#40A9FF'
    }
  },
  // 极简配色方案
  palette: [
    '#FF6A00',
    '#00B42A',
    '#1890FF',
    '#FF7D00',
    '#722ED1',
    '#F53F3F',
    '#13C2C2',
    '#FA8C16',
    '#EB2F96',
    '#FAAD14'
  ]
};

// 预设常用图表配置
export const chartUtils = {
  // 柱状图默认配置
  getBarChartOptions: (title = '') => ({
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      title: {
        display: !!title,
        text: title,
        font: {
          size: 16,
          weight: '600',
          family: 'var(--font-family)'
        },
        color: 'var(--text-primary)'
      },
      legend: {
        display: true,
        position: 'top',
        align: 'end',
        labels: {
          font: {
            size: 13,
            family: 'var(--font-family)'
          },
          color: 'var(--text-secondary)',
          padding: 12,
          usePointStyle: true,
          boxWidth: 6
        }
      },
      tooltip: {
        backgroundColor: 'rgba(255, 255, 255, 0.98)',
        titleColor: 'var(--text-primary)',
        bodyColor: 'var(--text-secondary)',
        borderColor: 'var(--border-color)',
        borderWidth: 1,
        borderRadius: 6,
        boxPadding: 8,
        padding: 10,
        displayColors: true,
        callbacks: {
          label: function(context) {
            return `${context.dataset.label}: ${context.parsed.y}`;
          }
        },
        titleFont: {
          size: 14,
          weight: '600'
        },
        bodyFont: {
          size: 13
        }
      }
    },
    scales: {
      x: {
        grid: {
          display: false,
          drawBorder: false
        },
        ticks: {
          color: 'var(--text-tertiary)',
          font: {
            size: 12,
            family: 'var(--font-family)'
          },
          padding: 8
        },
        border: {
          color: 'var(--border-color)'
        }
      },
      y: {
        beginAtZero: true,
        grid: {
          color: 'var(--border-color)',
          drawBorder: false,
          lineWidth: 0.5
        },
        ticks: {
          color: 'var(--text-tertiary)',
          font: {
            size: 12,
            family: 'var(--font-family)'
          },
          padding: 8
        },
        border: {
          display: false
        }
      }
    },
    elements: {
      bar: {
        borderRadius: 6,
        borderSkipped: false,
        backgroundColor: miChartColors.primary,
        hoverBackgroundColor: miChartColors.secondary,
        borderWidth: 0
      }
    },
    animation: {
      duration: 800,
      easing: 'easeInOutQuart'
    }
  }),
  
  // 折线图默认配置
  getLineChartOptions: (title = '') => ({
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      title: {
        display: !!title,
        text: title,
        font: {
          size: 16,
          weight: '600',
          family: 'var(--font-family)'
        },
        color: 'var(--text-primary)'
      },
      legend: {
        display: true,
        position: 'top',
        align: 'end',
        labels: {
          font: {
            size: 13,
            family: 'var(--font-family)'
          },
          color: 'var(--text-secondary)',
          padding: 12,
          usePointStyle: true,
          boxWidth: 6
        }
      },
      tooltip: {
        backgroundColor: 'rgba(255, 255, 255, 0.98)',
        titleColor: 'var(--text-primary)',
        bodyColor: 'var(--text-secondary)',
        borderColor: 'var(--border-color)',
        borderWidth: 1,
        borderRadius: 6,
        boxPadding: 8,
        padding: 10,
        displayColors: true,
        titleFont: {
          size: 14,
          weight: '600'
        },
        bodyFont: {
          size: 13
        }
      }
    },
    scales: {
      x: {
        grid: {
          display: false,
          drawBorder: false
        },
        ticks: {
          color: 'var(--text-tertiary)',
          font: {
            size: 12,
            family: 'var(--font-family)'
          },
          padding: 8
        },
        border: {
          color: 'var(--border-color)'
        }
      },
      y: {
        beginAtZero: true,
        grid: {
          color: 'var(--border-color)',
          drawBorder: false,
          lineWidth: 0.5
        },
        ticks: {
          color: 'var(--text-tertiary)',
          font: {
            size: 12,
            family: 'var(--font-family)'
          },
          padding: 8
        },
        border: {
          display: false
        }
      }
    },
    elements: {
      line: {
        borderColor: miChartColors.primary,
        borderWidth: 2,
        tension: 0.4,
        fill: true,
        backgroundColor: 'rgba(255, 106, 0, 0.08)'
      },
      point: {
        radius: 4,
        backgroundColor: miChartColors.primary,
        borderColor: '#FFFFFF',
        borderWidth: 2,
        hoverRadius: 6,
        hoverBackgroundColor: miChartColors.secondary
      }
    },
    animation: {
      duration: 1000,
      easing: 'easeInOutQuart'
    }
  }),
  
  // 饼图默认配置
  getPieChartOptions: (title = '') => ({
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      title: {
        display: !!title,
        text: title,
        font: {
          size: 16,
          weight: '600',
          family: 'var(--font-family)'
        },
        color: 'var(--text-primary)'
      },
      legend: {
        display: true,
        position: 'right',
        labels: {
          font: {
            size: 13,
            family: 'var(--font-family)'
          },
          color: 'var(--text-secondary)',
          padding: 12,
          usePointStyle: true,
          boxWidth: 6
        }
      },
      tooltip: {
        backgroundColor: 'rgba(255, 255, 255, 0.98)',
        titleColor: 'var(--text-primary)',
        bodyColor: 'var(--text-secondary)',
        borderColor: 'var(--border-color)',
        borderWidth: 1,
        borderRadius: 6,
        boxPadding: 8,
        padding: 10,
        displayColors: true,
        titleFont: {
          size: 14,
          weight: '600'
        },
        bodyFont: {
          size: 13
        }
      }
    },
    elements: {
      arc: {
        borderColor: '#FFFFFF',
        borderWidth: 2
      }
    },
    animation: {
      animateScale: true,
      animateRotate: true,
      duration: 1000,
      easing: 'easeInOutQuart'
    }
  }),
  
  // 获取小米配色方案
  getMiPalette: () => miChartColors.palette,
  
  // 获取渐变色配置
  getGradientConfig: (ctx, colorName = 'primary') => {
    const gradient = ctx.createLinearGradient(0, 0, 0, 400);
    gradient.addColorStop(0, miChartColors.gradient[colorName].from);
    gradient.addColorStop(1, miChartColors.gradient[colorName].to);
    return gradient;
  }
};

export default ChartComponent;