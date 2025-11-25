import React from 'react';
import { Card, Statistic, Row, Col } from 'antd';
import { ArrowUpOutlined, ArrowDownOutlined } from '@ant-design/icons';

const StatCard = ({ title, value, prefix, suffix, color = '#3f8600', loading = false }) => {
  return (
    <Card className="stat-card" bordered={false}>
      <Row gutter={16}>
        <Col span={24}>
          <Statistic
            title={title}
            value={value}
            prefix={prefix}
            suffix={suffix}
            valueStyle={{ color }}
            loading={loading}
          />
        </Col>
      </Row>
    </Card>
  );
};

// 组合多个统计卡片的组件
export const StatCardGroup = ({ stats, loading = false }) => {
  return (
    <Row gutter={[16, 16]}>
      {stats.map((stat, index) => (
        <Col xs={24} sm={12} md={8} lg={6} key={index}>
          <StatCard
            title={stat.title}
            value={stat.value}
            prefix={stat.prefix}
            suffix={stat.suffix}
            color={stat.color}
            loading={loading}
          />
        </Col>
      ))}
    </Row>
  );
};

export default StatCard;