import { useState, useEffect } from 'react'
import './App.css'
import { Layout, Menu, Typography, Row, Col, message } from 'antd'
import { UploadOutlined, BarChartOutlined, LineChartOutlined, HeartOutlined, PieChartOutlined, CalendarOutlined } from '@ant-design/icons'
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
    <Layout className="layout" style={{ minHeight: '100vh' }}>
      <Header>
        <div className="logo" />
        <Menu
          theme="dark"
          mode="horizontal"
          selectedKeys={[current]}
          onClick={handleMenuClick}
          style={{ minWidth: 0, flex: 1, maxWidth: '100%' }}
        >
          <Menu.Item key="upload" icon={<UploadOutlined />}>数据导入</Menu.Item>
          <Menu.Item key="overall" icon={<BarChartOutlined />}>总体统计</Menu.Item>
          <Menu.Item key="trends" icon={<LineChartOutlined />}>运动趋势</Menu.Item>
          <Menu.Item key="heartRate" icon={<HeartOutlined />}>心率分析</Menu.Item>
          <Menu.Item key="activityTypes" icon={<PieChartOutlined />}>活动类型</Menu.Item>
          <Menu.Item key="recent" icon={<CalendarOutlined />}>最近活动</Menu.Item>
        </Menu>
      </Header>
      <Content style={{ padding: '0 50px' }}>
        <div className="site-layout-content" style={{ padding: 24, background: '#fff', minHeight: 360 }}>
          {/* API连接状态显示 */}
          <div style={{ 
            marginBottom: '20px', 
            padding: '10px', 
            backgroundColor: apiConnected ? 
              (apiError ? '#fffbe6' : '#f6ffed') : '#fff2f0', 
            border: '1px solid', 
            borderColor: apiConnected ? 
              (apiError ? '#ffe58f' : '#b7eb8f') : '#ffccc7', 
            borderRadius: '4px' 
          }}>
            <p style={{ margin: 0, marginBottom: '8px' }}>
              API连接状态: 
              {apiConnected ? (
                apiError ? (
                  <span>
                    <span style={{ color: '#faad14' }}>已连接</span>
                    <span style={{ marginLeft: '10px', color: '#faad14' }}>提示: {apiError}</span>
                    <span style={{ marginLeft: '10px', fontSize: '12px', color: '#666' }}>
                      (请先使用文件上传功能导入数据)
                    </span>
                  </span>
                ) : (
                  <span style={{ color: '#52c41a' }}>已连接并正常工作</span>
                )
              ) : (
                <span style={{ color: '#ff4d4f' }}>服务不可用 ({apiError})</span>
              )}
            </p>
            
            {/* 详细诊断信息 */}
            {apiTestInfo && (
              <div style={{ 
                fontSize: '12px', 
                color: '#666', 
                backgroundColor: '#fafafa', 
                padding: '8px', 
                borderRadius: '4px',
                maxHeight: '200px',
                overflow: 'auto'
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
          {renderContent()}
        </div>
      </Content>
      <Footer style={{ textAlign: 'center' }}>佳明运动数据分析平台 ©2024</Footer>
    </Layout>
  )
}

export default App