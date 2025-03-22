import { http } from '@/utils/http'

// 发送对话
export function SendMessageToAi(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/chat/sendChat',
        params: params
    })
}

// 获取最大对话
export function GetMaxChatData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/chat/getMaxChatId',
        params: params
    })
}

// 接收对话
export function GetChatDetailData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/chat/getChat',
        params: params
    })
}

