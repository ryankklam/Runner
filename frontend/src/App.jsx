import { useState } from 'react'
import './App.css'
import { Layout, Menu, Typography, Row, Col } from 'antd'
import { UploadOutlined, BarChartOutlined, LineChartOutlined, HeartOutlined, PieChartOutlined, RecentOutlined } from '@ant-design/icons'
import FileUploader from './components/FileUploader'
import { StatCardGroup } from './components/StatCard'
import ChartComponent, { chartUtils } from './components/ChartComponent'
import Dashboard from './components/Dashboard'

const { Header, Content, Footer } = Layout
const { Title, Text } = Typography

function App() {
  const [current, setCurrent] = useState('upload')

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
          <Menu.Item key="recent" icon={<RecentOutlined />}>最近活动</Menu.Item>
        </Menu>
      </Header>
      <Content style={{ padding: '0 50px' }}>
        <div className="site-layout-content" style={{ padding: 24, background: '#fff', minHeight: 360 }}>
          {renderContent()}
        </div>
      </Content>
      <Footer style={{ textAlign: 'center' }}>佳明运动数据分析平台 ©2024</Footer>
    </Layout>
  )
}

export default App