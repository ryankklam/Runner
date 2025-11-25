const http = require('http');

// 模拟前端API测试
function testApi() {
  console.log('开始测试API连接...');
  
  const options = {
    hostname: 'localhost',
    port: 8080,
    path: '/api/statistics/overall',
    method: 'GET',
    timeout: 5000,
    headers: {
      'Content-Type': 'application/json'
    }
  };
  
  console.log('测试URL:', `http://${options.hostname}:${options.port}${options.path}`);
  
  const req = http.request(options, (res) => {
    console.log(`API连接状态: ${res.statusCode}`);
    
    let data = '';
    res.on('data', (chunk) => {
      data += chunk;
    });
    
    res.on('end', () => {
      console.log('响应数据:', data);
      if (res.statusCode >= 200 && res.statusCode < 300) {
        console.log('✅ API连接成功!');
      } else {
        console.error('❌ API返回错误状态码');
      }
    });
  });
  
  req.on('error', (error) => {
    console.error('❌ API连接失败:');
    console.error('错误类型:', error.code);
    console.error('错误信息:', error.message);
    if (error.code === 'ECONNREFUSED') {
      console.error('服务器拒绝连接，可能是服务未启动或端口错误');
    } else if (error.code === 'ECONNRESET') {
      console.error('连接被重置');
    } else if (error.code === 'ETIMEDOUT') {
      console.error('连接超时');
    }
  });
  
  req.on('timeout', () => {
    console.error('❌ 请求超时');
    req.destroy();
  });
  
  req.end();
}

// 运行测试
testApi();