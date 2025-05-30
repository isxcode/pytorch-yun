/*
 * @Author: fanciNate
 * @Date: 2023-04-26 17:01:16
 * @LastEditTime: 2023-06-18 13:07:27
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /pytorch-yun/pytorch-yun-website/src/services/computer-group.service.ts
 */
import { http } from '@/utils/http'
interface SerchParams {
  page: number;
  pageSize: number;
  searchKeyWord: string;
}

interface AddParams {
  remark: string;
  name: string;
  id?: string;
}

interface Cluster {
  clusterId: string
}

export function GetComputerGroupList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster/pageCluster',
    params: params
  })
}

// 添加集群
export function AddComputerGroupData(params: AddParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster/addCluster',
    params: params
  })
}

// 更新集群
export function UpdateComputerGroupData(params: AddParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster/updateCluster',
    params: params
  })
}

// 设置默认集群
export function SetDefaultComputerGroup(params: Cluster): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster/setDefaultCluster',
    params: params
  })
}

// 检测
export function CheckComputerGroupData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster/checkCluster',
    params: params
  })
}

// 删除
export function DeleteComputerGroupData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster/deleteCluster',
    params: params
  })
}

// 节点页面查询数据
export function GetComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster-node/pageClusterNode',
    params: params
  })
}

// 检测节点页面查询数据
export function CheckComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster-node/checkAgent',
    params: params
  })
}

// 设置默认集群节点
export function SetDefaultComputerPointNode(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster-node/setDefaultClusterNode',
    params: params
  })
}

// 编辑节点页面查询数据
export function UpdateComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster-node/updateClusterNode',
    params: params
  })
}

// 添加节点数据
export function AddComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster-node/addClusterNode',
    params: params
  })
}

// 编辑节点数据
export function EditComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster-node/updateClusterNode',
    params: params
  })
}

// 添加节点数据
export function DeleteComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster-node/deleteClusterNode',
    params: params
  })
}

// 安装节点数据
export function InstallComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster-node/installAgent',
    params: params
  })
}

// 卸载节点数据
export function UninstallComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster-node/removeAgent',
    params: params
  })
}

// 清理节点数据
export function CleanComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster-node/cleanAgent',
    params: params
  })
}

// 停止节点数据
export function StopComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster-node/stopAgent',
    params: params
  })
}

// 激活节点数据
export function StartComputerPointData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster-node/startAgent',
    params: params
  })
}

// 获取节点数据
export function GetComputerPointDetailData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster-node/getClusterNode',
    params: params
  })
}

// 链接测试接口
export function TestComputerPointHostData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/cluster-node/testAgent',
    params: params
  })
}
