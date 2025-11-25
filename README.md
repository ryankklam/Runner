# 佳明运动数据分析平台

## 项目简介
这是一个专门用于分析佳明(Garmin)运动手表数据的平台，支持CSV格式的数据导入、统计分析和可视化展示。通过该平台，用户可以深入了解自己的运动习惯、表现趋势和健康状况。

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database (开发环境)
- MySQL/PostgreSQL (生产环境)

### 前端
- React 18
- Ant Design
- Chart.js
- Axios
- Vite

## 项目结构

```
Runner/
├── backend/                    # 后端Spring Boot项目
│   ├── src/main/java/com/runner/
│   │   ├── config/             # 配置类
│   │   ├── controller/         # 控制器
│   │   ├── model/              # 数据模型
│   │   ├── repository/         # 数据访问层
│   │   ├── service/            # 业务逻辑层
│   │   └── util/               # 工具类
│   └── pom.xml                 # Maven配置文件
├── frontend/                   # 前端React项目
│   ├── public/                 # 静态资源
│   ├── src/
│   │   ├── assets/             # 资源文件
│   │   ├── components/         # React组件
│   │   ├── services/           # API服务
│   │   ├── App.jsx             # 主应用组件
│   │   └── main.jsx            # 入口文件
│   ├── index.html              # HTML入口
│   ├── package.json            # npm配置
│   └── vite.config.js          # Vite配置
└── README.md                   # 项目说明（当前文件）
```

## 功能特性

### 1. 数据导入
- 支持佳明手表导出的CSV格式运动数据文件上传
- 自动解析和验证数据格式
- 数据导入状态反馈

### 2. 统计分析
- 总体运动数据概览（距离、时长、卡路里等）
- 运动类型分布分析
- 运动强度分析
- 运动趋势分析（支持周、月、年视图切换）

### 3. 可视化展示
- 统计卡片展示关键指标
- 饼图展示运动类型分布
- 折线图展示运动趋势
- 柱状图展示运动强度分析

## 如何运行

### 后端运行

1. 确保已安装Java 17和Maven
2. 进入后端目录：
```bash
cd backend
```
3. 编译并运行项目：
```bash
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动。

### 前端运行

1. 确保已安装Node.js (推荐v16+)
2. 进入前端目录：
```bash
cd frontend
```
3. 安装依赖：
```bash
npm install
```
4. 启动开发服务器：
```bash
npm run dev
```

前端服务将在 http://localhost:3000 启动。

## API接口说明

### 数据导入接口
- URL: `/api/activities/upload`
- Method: POST
- Content-Type: multipart/form-data
- 上传字段名: `file` (CSV文件)

### 获取统计数据接口
- URL: `/api/statistics/overall`
- Method: GET
- 返回: 包含总体统计数据的JSON对象

### 获取运动类型统计接口
- URL: `/api/statistics/activity-types`
- Method: GET
- 返回: 各运动类型统计数据的JSON对象

### 获取运动趋势接口
- URL: `/api/statistics/trends?timeRange={week|month|year}`
- Method: GET
- 参数: `timeRange` - 时间范围（周、月、年）
- 返回: 运动趋势数据的JSON对象

## 数据格式要求

上传的CSV文件应包含以下字段（必须为英文表头）：

- `Activity Type` - 运动类型
- `Date` - 日期
- `Duration` - 时长（分钟）
- `Distance` - 距离（公里）
- `Calories` - 卡路里消耗
- `Avg Heart Rate` - 平均心率（可选）
- `Max Heart Rate` - 最大心率（可选）

## 注意事项

1. 后端使用内存数据库H2，重启服务后数据会重置。生产环境应配置持久化数据库。
2. 文件上传有大小限制，默认不超过10MB。
3. 系统会自动清理重复导入的数据。

## 许可证

本项目采用MIT许可证。