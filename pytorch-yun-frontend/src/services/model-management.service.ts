import { http } from '@/utils/http'

export interface QueryParams {
    page: number
    pageSize: number
    searchKeyWord?: string
}

export function QueryModelList(params: QueryParams): Promise<any> {
    return http.request({
        method: 'post',
        url: '/model/pageModel',
        params: params
    })
}
