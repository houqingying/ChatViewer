import base from './index'
const axios = base.axios
const baseUrl = base.baseUrl

// 获取好友
export const getFriend = () => {
    return axios({
      method: 'get',
      baseURL: `${baseUrl}/message`,
    }).then(res => res.data.data)
}

// 获取聊天信息
export const getChatMsg = params => {
  return axios({
    method: 'get',
    baseURL: `${baseUrl}/message/conversation?id1=`+ params.id1,
  }).then(res => res.data.data.records)
}

// 发送聊天信息
export const sendChatMsg = params => {
    return axios({
        method: 'post',
        baseURL: `${baseUrl}/message`,
        data: params
    })
}

// 将所有聊天信息设为已读
export const readChatMsg = params => {
    return axios({
        method: 'post',
        baseURL: `${baseUrl}/message/read?id1=`+ params.id1,
    })
}

// 获取chatgpt历史对话
export const getChatgptLog = () => {
    return axios({
        method: 'get',
        baseURL: `${baseUrl}/gpt/conversation`,
    }).then(res => res.data.data)
}

// 创建chatgpt新对话
export const createChatgptConversation = params => {
    return axios({
        method: 'post',
        baseURL: `${baseUrl}/gpt/conversation`,
        data: params
    }).then(res => res.data.data)
}

// 获取chatgpt历史对话
export const getGptMessage = conversationId => {
    return axios({
        method: 'get',
        baseURL: `${baseUrl}/gpt/chat/message?conversationId=`+ conversationId,
    }).then(res => res.data.data)
}

// 删除chatGPT对话
export const deleteChatgptConversation = id => {
    return axios({
        method: 'delete',
        baseURL: `${baseUrl}/gpt/conversation/` + id,
    })
}
