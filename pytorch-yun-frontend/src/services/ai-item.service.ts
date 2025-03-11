import { http } from '@/utils/http'

export interface QueryParams {
    page: number
    pageSize: number
    searchKeyWord?: string
}

// 查询
export function QueryAiItemList(params: QueryParams): Promise<any> {
    return http.request({
        method: 'post',
        url: '/ai/pageAi',
        params: params
    })
}

// 新建
export function AddAiItemData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/ai/addAi',
        params: params
    })
}

// 更新
export function UpdateAiItemData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/ai/updateAi',
        params: params
    })
}

