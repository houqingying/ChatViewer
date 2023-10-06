
export const phoneRule = /^(?:(?:\+|00)86)?1(?:(?:3[\d])|(?:4[5-79])|(?:5[0-35-9])|(?:6[5-7])|(?:7[0-8])|(?:8[\d])|(?:9[189]))\d{8}$/

export const isPhoneNumber = (params) => {
  const regex = new RegExp(phoneRule)
  return regex.test(params)
}

export const convertArray2Date = (params) => {
  return params[0] + '-' + params[1] + '-' + params[2]
}

// 示例用法 const dateTime = getCurrentDateTime(); console.log(dateTime); // 输出类似于 '2022-01-01 12:30' 的时间字符串
export const getCurrentDateTime = () => {
  const date = new Date();
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}`;
}
