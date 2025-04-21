<template>
    <BlockModal :model-config="modelConfig">
        <el-form ref="form" class="add-computer-group" label-position="top" :model="formData" :rules="rules">
            <el-form-item label="名称" prop="name">
                <el-input
                    v-model="formData.name"
                    maxlength="200"
                    placeholder="请输入"
                />
            </el-form-item>
            <el-form-item label="类型" prop="type">
                <el-select v-model="formData.type" disabled placeholder="请选择">
                    <el-option
                        v-for="item in typeList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="模型" prop="modelId">
                <el-select v-model="formData.modelId" placeholder="请选择">
                    <el-option
                        v-for="item in modelIdList"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="Key" prop="authConfig.apiKey">
                <el-input v-model="formData.authConfig.apiKey" maxlength="100" type="password" show-password placeholder="请输入" />
            </el-form-item>
            <el-form-item label="备注">
                <el-input v-model="formData.remark" show-word-limit type="textarea" maxlength="200"
                    :autosize="{ minRows: 4, maxRows: 4 }" placeholder="请输入" />
            </el-form-item>
        </el-form>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { QueryModelList } from '@/services/model-management.service';

interface ApiKey {
    apiKey: string
}

interface FormParams {
    name: string
    modelId: string
    remark: string
    authConfig: ApiKey
    type: string
    id?: string
}

interface Option {
    label: string
    value: string
}

const form = ref<FormInstance>()
const callback = ref<any>()
const renderSence = ref('new')
const typeList = ref<Option[]>([
    {
        label: '本地部署',
        value: 'local'
    },
    {
        label: 'API',
        value: 'API'
    }
])
const modelIdList = ref<any[]>([])
const modelConfig = reactive({
    title: '添加',
    visible: false,
    width: '520px',
    okConfig: {
        title: '确定',
        ok: okEvent,
        disabled: false,
        loading: false
    },
    cancelConfig: {
        title: '取消',
        cancel: closeEvent,
        disabled: false
    },
    needScale: false,
    zIndex: 1100,
    closeOnClickModal: false
})
const formData = reactive<FormParams>({
    name: '',
    modelId: '',
    remark: '',
    type: 'API',
    authConfig: {
        apiKey: ''
    },
    id: ''
})
const rules = reactive<FormRules>({
    name: [{ required: true, message: '请输入名称', trigger: ['change', 'blur']}],
    type: [{ required: true, message: '请选择类型', trigger: ['change', 'blur']}],
    modelId: [{ required: true, message: '请选择模型', trigger: ['change', 'blur']}],
    'authConfig.apiKey': [{ required: true, message: '请输入Key', trigger: ['change', 'blur']}],
})

function showModal(cb: () => void, data: any): void {
    callback.value = cb
    getModelListOptions()
    if (data) {
        modelConfig.title = '编辑'
        renderSence.value = 'edit'
        Object.keys(formData).forEach((key: string) => {
            if (key === 'authConfig' && !data[key]) {
                formData[key] = {
                    apiKey: ''
                }
            } else if (key === 'type' && !data[key]) {
                formData[key] = 'API'
            } else {
                formData[key] = data[key]
            }
        })
    } else {
        modelConfig.title = '添加'
        renderSence.value = 'new'
        formData.name = ''
        formData.modelId = ''
        formData.type = 'API'
        formData.remark = ''
        formData.authConfig = { apiKey: '' }
    }
    nextTick(() => {
        form.value?.resetFields()
    })
    modelConfig.visible = true
}

function okEvent() {
    form.value?.validate((valid: boolean) => {
        if (valid) {
            modelConfig.okConfig.loading = true
            callback.value({
                ...formData,
                id: formData.id ? formData.id : undefined
            }).then((res: any) => {
                modelConfig.okConfig.loading = false
                if (res === undefined) {
                    modelConfig.visible = false
                } else {
                    modelConfig.visible = true
                }
            }).catch(() => {
                modelConfig.okConfig.loading = false
            })
        } else {
            ElMessage.warning('请将表单输入完整')
        }
    })
}

function getModelListOptions() {
    QueryModelList({
        page: 0,
        pageSize: 9999,
        searchKeyWord: ''
    }).then((res: any) => {
        modelIdList.value = res.data.content
    }).catch(() => {
        modelIdList.value = []
    })
}

function closeEvent() {
    modelConfig.visible = false
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.add-computer-group {
    padding: 12px 20px 0 20px;
    box-sizing: border-box;
}
</style>
