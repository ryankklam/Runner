import { useState, useEffect } from 'react'
import './App.css'
import { Layout, Menu, Typography, Row, Col, message, ConfigProvider, Drawer, Button } from 'antd'
import { UploadOutlined, BarChartOutlined, LineChartOutlined, HeartOutlined, PieChartOutlined, CalendarOutlined, MenuOutlined } from '@ant-design/icons'
import FileUploader from './components/FileUploader'
import { StatCardGroup } from './components/StatCard'
import ChartComponent, { chartUtils } from './components/ChartComponent'
import Dashboard from './components/Dashboard'
import { statisticsAPI } from './services/api'

const { Header, Content, Footer } = Layout
const { Title, Text } = Typography

function App() {
    const [current, setCurrent] = useState('upload')
    const [apiConnected, setApiConnected] = useState(false)
    const [apiError, setApiError] = useState(null)
    const [apiTestInfo, setApiTestInfo] = useState(null)
    const [mobileMenuVisible, setMobileMenuVisible] = useState(false)
    const [isMobile, setIsMobile] = useState(window.innerWidth <= 768)
    
    // 监听窗口大小变化，判断是否为移动端
    useEffect(() => {
      const handleResize = () => {
        setIsMobile(window.innerWidth <= 768)
      }
      
      window.addEventListener('resize', handleResize)
      return () => window.removeEventListener('resize', handleResize)
    }, [])

    // 组件挂载时测试API连接
  useEffect(() => {
    const testApiConnection = async () => {
      try {
        console.log('测试API连接...')
        // 保存API测试的详细信息
        const apiTestInfo = {
          timestamp: new Date().toLocaleString(),
          testUrl: 'http://localhost:8080/api/statistics/overall',
          status: '测试中',
          errorDetails: null
        };
        setApiTestInfo(apiTestInfo);
        
        // 直接使用fetch API进行测试，避免依赖问题
        const response = await fetch('http://localhost:8080/api/statistics/overall');
        
        if (response.ok) {
          const data = await response.json();
          apiTestInfo.status = '成功';
          apiTestInfo.response = data;
          setApiConnected(true);
          setApiError(null);
        } else {
          const errorData = await response.json().catch(() => ({}));
          apiTestInfo.status = `失败 (HTTP ${response.status})`;
          apiTestInfo.errorDetails = errorData.message || '未知错误';
          setApiConnected(true); // 服务是可访问的，但返回了错误
          setApiError('服务已连接，但可能需要先上传数据');
        }
        
        setApiTestInfo({...apiTestInfo});
      } catch (error) {
        console.error('API响应错误:', error);
        // 保存详细错误信息
        const apiTestInfo = {
          timestamp: new Date().toLocaleString(),
          testUrl: 'http://localhost:8080/api/statistics/overall',
          status: '连接失败',
          errorDetails: error.message || '未知错误',
          errorType: error.name || 'Error'
        };
        setApiTestInfo(apiTestInfo);
        
        setApiConnected(false);
        setApiError('网络连接失败，请检查后端服务');
      }
    };

    testApiConnection();
  }, [])
  
    const handleMenuClick = (e) => {
      setCurrent(e.key)
      // 关闭移动端菜单
      setMobileMenuVisible(false)
    }

  // 模拟数据 - 实际应用中会从API获取
  const mockOverallStats = [
    { title: '总距离', value: 1256.8, suffix: '公里', color: '#1890ff' },
    { title: '活动次数', value: 238, suffix: '次', color: '#52c41a' },
    { title: '总时长', value: 189.5, suffix: '小时', color: '#faad14' },
    { title: '消耗卡路里', value: 156800, suffix: '千卡', color: '#f5222d' },
  ]

  const mockActivityTypeData = {
    labels: ['跑步', '骑行', '游泳', '徒步', '其他'],
    datasets: [{
      data: [145, 62, 18, 10, 3],
      backgroundColor: ['#1890ff', '#52c41a', '#faad14', '#722ed1', '#fa8c16']
    }]
  }

  const mockMonthlyTrendData = {
    labels: ['2024-06', '2024-07', '2024-08', '2024-09', '2024-10', '2024-11'],
    datasets: [{
      label: '距离(公里)',
      data: [215, 189, 234, 178, 245, 221],
      borderColor: '#1890ff',
      backgroundColor: 'rgba(24, 144, 255, 0.1)',
      tension: 0.4
    }]
  }

  const renderContent = () => {
    switch (current) {
      case 'upload':
        return (
          <div className="page-content">
            <Title level={2}>导入佳明运动数据</Title>
            <Text>上传您的佳明手表运动数据CSV文件进行分析</Text>
            <div className="upload-area">
              <FileUploader />
            </div>
          </div>
        )
      case 'overall':
        return (
          <div className="page-content">
            <Dashboard />
          </div>
        )
      case 'trends':
        return (
          <div className="page-content">
            <Title level={2}>运动趋势</Title>
            <Text>查看您的运动趋势分析</Text>
            
            <Row gutter={16} style={{ marginTop: 20 }}>
              <Col span={24}>
                <ChartComponent 
                  type="line" 
                  data={mockMonthlyTrendData} 
                  options={chartUtils.getLineChartOptions('月度运动距离趋势')}
                  title="月度运动距离趋势"
                />
              </Col>
            </Row>
          </div>
        )
      case 'heartRate':
        return (
          <div className="page-content">
            <Title level={2}>心率分析</Title>
            <Text>查看您的心率区间分布</Text>
            
            <Row gutter={16} style={{ marginTop: 20 }}>
              <Col span={24}>
                <ChartComponent 
                  type="bar" 
                  data={{
                    labels: ['恢复区', '有氧区', '阈值区', '无氧区', '极限区'],
                    datasets: [{
                      label: '活动次数',
                      data: [35, 120, 55, 25, 3],
                      backgroundColor: '#1890ff'
                    }]
                  }} 
                  options={chartUtils.getBarChartOptions('心率区间分布')}
                  title="心率区间分布"
                />
              </Col>
            </Row>
          </div>
        )
      case 'activityTypes':
        return (
          <div className="page-content">
            <Title level={2}>活动类型统计</Title>
            <Text>查看您的各种运动类型分布</Text>
            
            <Row gutter={16} style={{ marginTop: 20 }}>
              <Col xs={24} lg={12}>
                <ChartComponent 
                  type="pie" 
                  data={mockActivityTypeData} 
                  options={chartUtils.getPieChartOptions('活动类型占比')}
                  title="活动类型占比"
                />
              </Col>
              <Col xs={24} lg={12}>
                <ChartComponent 
                  type="bar" 
                  data={{
                    labels: ['跑步', '骑行', '游泳', '徒步', '其他'],
                    datasets: [{
                      label: '总距离(公里)',
                      data: [850, 320, 45, 40, 1.8],
                      backgroundColor: '#52c41a'
                    }]
                  }} 
                  options={chartUtils.getBarChartOptions('各类型总距离')}
                  title="各类型总距离"
                />
              </Col>
            </Row>
          </div>
        )
      case 'recent':
        return (
          <div className="page-content">
            <Title level={2}>最近活动</Title>
            <Text>查看您的最近运动记录</Text>
            
            <div style={{ marginTop: 20, padding: 20, backgroundColor: '#fafafa', borderRadius: 8 }}>
              <p>最近活动列表功能开发中...</p>
            </div>
          </div>
        )
      default:
        return (
          <div className="page-content">
            <Title level={2}>欢迎使用佳明运动数据分析平台</Title>
            <Text>请选择左侧菜单查看相应功能</Text>
          </div>
        )
    }
  }

  return (
    <ConfigProvider
      theme={{
        token: {
          // 极简现代风格 - 色彩配置
          colorPrimary: '#FF6A00',
          colorSuccess: '#00B42A',
          colorWarning: '#FF7D00',
          colorError: '#F53F3F',
          
          // 极简现代风格 - 背景色配置
          colorBgContainer: '#FFFFFF',
          colorBgLayout: '#F8F8F8',
          colorBgElevated: '#FFFFFF',
          colorBgSpotlight: '#FFFFFF',
          colorBgMask: 'rgba(0, 0, 0, 0.45)',
          
          // 极简现代风格 - 文本色配置
          colorTextPrimary: '#222222',
          colorTextSecondary: '#666666',
          colorTextTertiary: '#999999',
          colorTextQuaternary: '#CCCCCC',
          
          // 极简现代风格 - 边框色配置
          colorBorder: '#EEEEEE',
          colorBorderSecondary: '#F0F0F0',
          colorSplit: '#EEEEEE',
          
          // 极简现代风格 - 圆角配置
          borderRadius: 8,
          borderRadiusSM: 8,
          borderRadiusMD: 8,
          borderRadiusLG: 8,
          
          // 极简现代风格 - 字体配置
          fontSize: 16,
          fontSizeSM: 14,
          fontSizeLG: 18,
          fontSizeXL: 20,
          
          // 极简现代风格 - 间距配置
          lineHeight: 1.6,
          lineHeightLG: 1.8,
          
          // 极简现代风格 - 阴影配置
          boxShadow: '0 1px 3px rgba(0, 0, 0, 0.05)',
          boxShadowSecondary: '0 2px 8px rgba(0, 0, 0, 0.08)',
          boxShadowTertiary: '0 4px 12px rgba(0, 0, 0, 0.1)',
          
          // 极简现代风格 - 其他配置
          controlHeight: 36,
          controlHeightLG: 40,
          controlHeightSM: 32,
          padding: 16,
          paddingLG: 24,
          paddingSM: 12,
          paddingXS: 8,
          margin: 16,
          marginLG: 24,
          marginSM: 12,
          marginXS: 8,
        },
        algorithm: () => ({
          // 自定义算法，确保主题一致性
          colorPrimary: '#FF6A00',
          borderRadius: 8,
        })
      }}
    >
      <Layout className="layout" style={{ minHeight: '100vh', background: 'var(--bg-light)', margin: 0, padding: 0 }}>
        {/* 极简现代风格 - Header */}
        <Header style={{ 
          background: '#FFFFFF', 
          borderBottom: '1px solid var(--border-color)', 
          padding: '0 20px', 
          height: '56px', 
          lineHeight: '56px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          boxShadow: 'none'
        }}>
          {/* 极简Logo */}
          <div className="logo" style={{ 
            height: '32px', 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center', 
            color: 'var(--primary-color)', 
            fontWeight: '700', 
            fontSize: '18px' 
          }}>运动分析</div>
          
          {/* 桌面端菜单 */}
          {!isMobile && (
            <Menu
              theme="light"
              mode="horizontal"
              selectedKeys={[current]}
              onClick={handleMenuClick}
              style={{ 
                minWidth: 0, 
                flex: 1, 
                maxWidth: '100%', 
                background: 'transparent', 
                borderBottom: 'none',
                marginLeft: '32px',
                boxShadow: 'none'
              }}
              items={[
                { key: 'upload', icon: <UploadOutlined />, label: '数据导入' },
                { key: 'overall', icon: <BarChartOutlined />, label: '总体统计' },
                { key: 'trends', icon: <LineChartOutlined />, label: '运动趋势' },
                { key: 'heartRate', icon: <HeartOutlined />, label: '心率分析' },
                { key: 'activityTypes', icon: <PieChartOutlined />, label: '活动类型' },
                { key: 'recent', icon: <CalendarOutlined />, label: '最近活动' },
              ]}
              itemStyle={{ 
                margin: '0 4px',
                borderRadius: 'var(--border-radius-sm)',
                padding: '0 16px',
                height: '40px',
                lineHeight: '40px',
                transition: 'all 0.2s ease'
              }}
              selectedItemStyle={{ 
                backgroundColor: 'var(--primary-light)',
                color: 'var(--primary-color)',
                fontWeight: '500'
              }}
              hoverItemStyle={{ 
                backgroundColor: 'var(--bg-lighter)',
                color: 'var(--text-primary)',
                transition: 'all 0.2s ease'
              }}
              fontSize={14}
              style={{ 
                '& .ant-menu-item': {
                  transition: 'all 0.2s ease'
                },
                '& .ant-menu-item:hover': {
                  transform: 'translateY(0)',
                  backgroundColor: 'var(--bg-lighter)'
                },
                '& .ant-menu-item-selected': {
                  transform: 'translateY(0)',
                  backgroundColor: 'var(--primary-light)'
                }
              }}
            />
          )}
          
          {/* 移动端汉堡菜单按钮 */}
          {isMobile && (
            <Button
              type="text"
              icon={<MenuOutlined />}
              onClick={() => setMobileMenuVisible(true)}
              style={{ 
                fontSize: '20px',
                color: 'var(--text-primary)',
                padding: '4px',
                margin: 0,
                minWidth: 'auto'
              }}
            />
          )}
        </Header>
        
        {/* 移动端抽屉菜单 */}
        <Drawer
          title="运动分析"
          placement="left"
          onClose={() => setMobileMenuVisible(false)}
          open={mobileMenuVisible}
          bodyStyle={{ padding: 0, backgroundColor: '#FFFFFF' }}
          width={240}
          headerStyle={{ 
            background: '#FFFFFF',
            borderBottom: '1px solid var(--border-color)',
            padding: '16px 20px',
            fontSize: '16px',
            fontWeight: '600'
          }}
          drawerStyle={{ 
            boxShadow: 'var(--box-shadow-hover)'
          }}
        >
          <Menu
            theme="light"
            mode="vertical"
            selectedKeys={[current]}
            onClick={handleMenuClick}
            style={{ 
              width: '100%', 
              background: 'transparent', 
              borderRight: 'none'
            }}
            items={[
              { key: 'upload', icon: <UploadOutlined />, label: '数据导入' },
              { key: 'overall', icon: <BarChartOutlined />, label: '总体统计' },
              { key: 'trends', icon: <LineChartOutlined />, label: '运动趋势' },
              { key: 'heartRate', icon: <HeartOutlined />, label: '心率分析' },
              { key: 'activityTypes', icon: <PieChartOutlined />, label: '活动类型' },
              { key: 'recent', icon: <CalendarOutlined />, label: '最近活动' },
            ]}
            itemStyle={{ 
              padding: '12px 20px',
              margin: '0',
              borderRadius: 0,
            }}
            selectedItemStyle={{ 
              backgroundColor: 'var(--primary-light)',
              color: 'var(--primary-color)',
            }}
            fontSize={14}
          />
        </Drawer>
        
        {/* 极简现代风格 - Content */}
        <Content style={{ 
          padding: '20px', 
          minHeight: 280,
          margin: 0
        }}>
          <div className="site-layout-content" style={{ 
            padding: 20, 
            background: '#FFFFFF', 
            borderRadius: 'var(--border-radius-sm)',
            boxShadow: 'var(--box-shadow-light)',
            minHeight: 360 
          }}>
            {/* API连接状态显示 */}
            <div style={{ 
              marginBottom: '20px', 
              padding: '12px 16px', 
              backgroundColor: apiConnected ? 
                (apiError ? '#FFF8F0' : '#F6FFED') : '#FFF2F0', 
              border: '1px solid', 
              borderColor: apiConnected ? 
                (apiError ? '#FFE0C2' : '#E6F7EF') : '#FFE8E8', 
              borderRadius: 'var(--border-radius-sm)',
              transition: 'all 0.2s ease'
            }}>
              <p style={{ margin: 0, fontSize: '14px', fontWeight: '500' }}>
                API连接状态: 
                {apiConnected ? (
                  apiError ? (
                    <span>
                      <span style={{ color: 'var(--warning-color)' }}>已连接</span>
                      <span style={{ marginLeft: '8px', color: 'var(--warning-color)', fontSize: '13px' }}>提示: {apiError}</span>
                    </span>
                  ) : (
                    <span style={{ color: 'var(--success-color)' }}>已连接并正常工作</span>
                  )
                ) : (
                  <span style={{ color: 'var(--error-color)' }}>服务不可用 ({apiError})</span>
                )}
              </p>
              
              {/* 详细诊断信息 */}
              {apiTestInfo && (
                <div style={{ 
                  marginTop: '8px',
                  fontSize: '12px', 
                  color: 'var(--text-secondary)', 
                  backgroundColor: 'var(--bg-lighter)', 
                  padding: '10px 12px', 
                  borderRadius: 'var(--border-radius-sm)',
                  maxHeight: '180px',
                  overflow: 'auto',
                  border: '1px solid var(--border-color)'
                }}>
                  <p>测试时间: {apiTestInfo.timestamp}</p>
                  <p>测试URL: {apiTestInfo.testUrl}</p>
                  <p>测试状态: {apiTestInfo.status}</p>
                  {apiTestInfo.errorDetails && <p>错误详情: {apiTestInfo.errorDetails}</p>}
                  {apiTestInfo.errorType && <p>错误类型: {apiTestInfo.errorType}</p>}
                  {apiTestInfo.response && (
                    <p>响应数据: {JSON.stringify(apiTestInfo.response).substring(0, 100)}...</p>
                  )}
                </div>
              )}
            </div>
            
            {/* 页面内容 */}
            {renderContent()}
          </div>
        </Content>
        
        {/* 极简现代风格 - Footer */}
        <Footer style={{ 
          background: '#FFFFFF', 
          borderTop: '1px solid var(--border-color)', 
          textAlign: 'center', 
          padding: '16px 20px',
          color: 'var(--text-tertiary)',
          fontSize: '13px',
          boxShadow: 'none'
        }}>佳明运动数据分析平台 ©2024</Footer>
      </Layout>
    </ConfigProvider>
  )
}

export default App