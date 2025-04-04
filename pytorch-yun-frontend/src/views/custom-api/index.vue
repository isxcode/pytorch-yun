<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="zqy-seach-table driver-table">
        <div class="zqy-table-top">
            <el-button type="primary" @click="addData">添加接口</el-button>
            <div class="zqy-seach">
                <el-input
                    v-model="keyword"
                    placeholder="请输入名称/备注 回车进行搜索"
                    :maxlength="200"
                    clearable
                    @input="inputEvent"
                    @keyup.enter="initData(false)"
                />
            </div>
        </div>
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="initData(false)">
            <div class="zqy-table">
                <BlockTable
                    :table-config="tableConfig"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                >
                    <template #statusTag="scopeSlot">
                        <ZStatusTag :status="scopeSlot.row.status"></ZStatusTag>
                    </template>
                    <template #options="scopeSlot">
                        <div class="btn-group">
                            <span v-if="scopeSlot.row.status !== 'UNPUBLISHED'" @click="underlineApi(scopeSlot.row)">下线</span>
                            <span v-if="scopeSlot.row.status === 'UNPUBLISHED'" @click="publishApi(scopeSlot.row)">发布</span>
                            <el-dropdown trigger="click">
                                <span class="click-show-more">更多</span>
                                <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item @click="editData(scopeSlot.row)">编辑</el-dropdown-item>
                                    <el-dropdown-item @click="testApi(scopeSlot.row)">测试</el-dropdown-item>
                                    <el-dropdown-item v-if="scopeSlot.row.status === 'UNPUBLISHED'" @click="deleteData(scopeSlot.row)">删除</el-dropdown-item>
                                    <!-- <el-dropdown-item>历史</el-dropdown-item> -->
                                </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                        </div>
                    </template>
                </BlockTable>
            </div>
        </LoadingPage>
        <AddModal ref="addModalRef" />
        <TestModal ref="testModalRef"/>
    </div>
</template>
  
  <script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import AddModal from './add-modal/index.vue'
import TestModal from './test-modal/index.vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import BlockTable from '@/components/block-table/index.vue'
import LoadingPage from '@/components/loading/index.vue'

import { BreadCrumbList, TableConfig } from './costom-api.config'
import { QueryCustomApiList, CreateCustomApiData, UpdateCustomApiData, DeleteCustomApiData, PublishCustomApiData, OfflineCustomApiData } from '@/services/custom-api.service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/useAuth'

const router = useRouter()
const authStore = useAuthStore()

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref()
const testModalRef = ref()

function initData(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    QueryCustomApiList({
        page: tableConfig.pagination.currentPage - 1,
        pageSize: tableConfig.pagination.pageSize,
        searchKeyWord: keyword.value
    }).then((res: any) => {
        tableConfig.tableData = res.data.content
        tableConfig.pagination.total = res.data.totalElements
        loading.value = false
        tableConfig.loading = false
        networkError.value = false
    }).catch(() => {
        tableConfig.tableData = [{}]
        tableConfig.pagination.total = 0
        loading.value = false
        tableConfig.loading = false
        networkError.value = true
    })
}

function addData() {
    addModalRef.value.showModal((data: any) => {
        return new Promise((resolve: any, reject: any) => {
            CreateCustomApiData(data).then((res: any) => {
                initData()
                ElMessage.success(res.msg)
                resolve()
            }).catch(err => {
                reject(err)
            })
        })
    })
}

function editData(row: any) {
    addModalRef.value.showModal((data: any) => {
        return new Promise((resolve: any, reject: any) => {
            UpdateCustomApiData(data).then((res: any) => {
                initData()
                ElMessage.success(res.msg)
                resolve()
            }).catch(err => {
                reject(err)
            })
        })
    }, row)
}

// 删除
function deleteData(data: any) {
    ElMessageBox.confirm('确定删除该接口吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        DeleteCustomApiData({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => {})
    })
}
// 下线
function underlineApi(data: any) {
    ElMessageBox.confirm('确定下线该接口吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        OfflineCustomApiData({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => {})
    })
}
// 发布接口
function publishApi(data: any) {
    ElMessageBox.confirm('确定发布该接口吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        PublishCustomApiData({
            id: data.id
        }).then((res: any) => {
            ElMessage.success(res.msg)
            initData()
        }).catch(() => {})
    })
}
// 测试api
function testApi(data: any) {
    testModalRef.value.showModal(data)
}

function inputEvent(e: string) {
    if (e === '') {
        initData()
    }
}

function handleSizeChange(e: number) {
    tableConfig.pagination.pageSize = e
    initData()
}

function handleCurrentChange(e: number) {
    tableConfig.pagination.currentPage = e
    initData()
}

onMounted(() => {
    tableConfig.pagination.currentPage = 1
    tableConfig.pagination.pageSize = 10
    initData()
})
</script>
    
<style lang="scss">
.zqy-seach-table {
    .name-click {
        cursor: pointer;
        color: getCssVar('color', 'primary', 'light-5');

        &:hover {
            color: getCssVar('color', 'primary');
        }
    }
    &.driver-table {
        .zqy-table {
            .btn-group {
                // justify-content: center;
            }
        }
    }
}
</style>
  