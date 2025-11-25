import React, { useState } from 'react';
import { Row, Col, Card, Typography, Select } from 'antd';
import StatCardGroup from './StatCard';
import ActivityTypeChart from './ActivityTypeChart';
import ActivityTrendChart from './ActivityTrendChart';
import IntensityAnalysisChart from './IntensityAnalysisChart';

const { Title, Text } = Typography;
const { Option } = Select;

const Dashboard = () => {
  const [timeRange, setTimeRange] = useState('week');

  // 模拟统计数据
  const statData = [
    { title: '总运动次数', value: '24', subtitle: '次', trend: '+12%', color: '#1890ff' },
    { title: '总运动距离', value: '82.5', subtitle: '公里', trend: '+8%', color: '#52c41a' },
    { title: '总运动时长', value: '18.5', subtitle: '小时', trend: '+5%', color: '#faad14' },
    { title: '消耗卡路里', value: '6850', subtitle: '千卡', trend: '+10%', color: '#f5222d' },
  ];

  const handleTimeRangeChange = (value) => {
    setTimeRange(value);
  };

  return (
    <div className="dashboard">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <div>
          <Title level={4} style={{ margin: 0 }}>运动数据总览</Title>
          <Text type="secondary">全面分析您的运动表现和习惯</Text>
        </div>
        <Select 
          value={timeRange} 
          onChange={handleTimeRangeChange}
          style={{ width: 120 }}
        >
          <Option value="week">本周</Option>
          <Option value="month">本月</Option>
          <Option value="year">本年</Option>
        </Select>
      </div>

      {/* 统计卡片区域 */}
      <StatCardGroup stats={statData} />

      {/* 图表区域 */}
      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} lg={8}>
          <ActivityTypeChart />
        </Col>
        <Col xs={24} lg={16}>
          <ActivityTrendChart timeRange={timeRange} />
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24}>
          <IntensityAnalysisChart />
        </Col>
      </Row>

      {/* 额外的统计卡片区域 */}
      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} md={8}>
          <Card className="stat-card">
            <div style={{ textAlign: 'center' }}>
              <Text type="secondary">平均配速</Text>
              <div style={{ marginTop: 12 }}>
                <Text strong style={{ fontSize: 32, color: '#1890ff' }}>6'25"</Text>
                <Text type="secondary" style={{ marginLeft: 8 }}>/公里</Text>
              </div>
              <div style={{ marginTop: 8 }}>
                <Text type="success">较上期提升5%</Text>
              </div>
            </div>
          </Card>
        </Col>
        <Col xs={24} md={8}>
          <Card className="stat-card">
            <div style={{ textAlign: 'center' }}>
              <Text type="secondary">最长单次距离</Text>
              <div style={{ marginTop: 12 }}>
                <Text strong style={{ fontSize: 32, color: '#52c41a' }}>12.8</Text>
                <Text type="secondary" style={{ marginLeft: 8 }}>公里</Text>
              </div>
              <div style={{ marginTop: 8 }}>
                <Text type="secondary">完成于上周末</Text>
              </div>
            </div>
          </Card>
        </Col>
        <Col xs={24} md={8}>
          <Card className="stat-card">
            <div style={{ textAlign: 'center' }}>
              <Text type="secondary">运动达标率</Text>
              <div style={{ marginTop: 12 }}>
                <Text strong style={{ fontSize: 32, color: '#faad14' }}>85%</Text>
              </div>
              <div style={{ marginTop: 8 }}>
                <Text type="secondary">已完成17/20个目标</Text>
              </div>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard;