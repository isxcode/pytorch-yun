<template>
    <BlockDrawer :drawer-config="drawerConfig">
        <el-scrollbar>
            <div class="app-config-container">
                <!-- 数据源配置 -->
                <div class="config-item">
                    <div class="item-title">基础配置</div>
                    <el-form
                        ref="formRef"
                        label-position="left"
                        label-width="120px"
                        :model="formData"
                        :rules="basicFormRules"
                    >
                        <el-form-item label="文字限制" prop="datasourceId">
                            <el-input-number v-model="formData.baseConfig.topK" :min="0" controls-position="right" />
                            <!-- <el-select v-model="formData.baseConfig.datasourceId" placeholder="请选择">
                                <el-option v-for="item in dataSourceList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select> -->
                        </el-form-item>
                        <el-form-item label="提示词" prop="datasourceId">
                            <el-input v-model="formData.prompt" placeholder="请输入"></el-input>
                            <!-- <el-select v-model="formData.baseConfig.datasourceId" placeholder="请选择">
                                <el-option v-for="item in dataSourceList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select> -->
                        </el-form-item>
                    </el-form>
                    <el-divider />
                </div>
            </div>
        </el-scrollbar>
    </BlockDrawer>
</template>

<script lang="ts" setup>
import { computed, nextTick, reactive, ref } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import BlockDrawer from '@/components/block-drawer/index.vue'
import { useRoute } from 'vue-router'
import { GetConfigAppData } from '@/services/app-management.service'

const route = useRoute()

const basicFormRules = ref([])
const formRef = ref<any>()
const callback = ref<any>()
const drawerConfig = reactive({
    title: '配置',
    visible: false,
    width: '400',
    okConfig: {
        title: '确定',
        ok: okEvent,
        disabled: false,
        loading: false,
    },
    cancelConfig: {
        title: '取消',
        cancel: closeEvent,
        disabled: false,
    },
    zIndex: 1100,
    closeOnClickModal: false,
})

const formData = reactive<{
    baseConfig: any
    prompt: any
    resources: any
}>({
    baseConfig: {
        topK: 0
    },
    prompt: '',
    resources: null
})

function showModal(cb: () => void, data: any) {
    callback.value = cb
    getConfigData()
    if (!data) {

    } else {

    }
    drawerConfig.visible = true;
}

function getConfigData() {
    GetConfigAppData({
        id: route.query.id
    }).then((res: any) => {
        formData.baseConfig = res.data.baseConfig || {
            topK: 0
        }
        formData.prompt = res.data.prompt || ''
        formData.resources = res.data.resources || []
    }).catch(() => {

    })
}

function okEvent() {
    // 获取cron表达式
    formRef.value?.validate((valid: boolean) => {
        if (valid) {
            callback.value(formData).then((res: any) => {
                drawerConfig.okConfig.loading = false
                if (res === undefined) {
                    drawerConfig.visible = false
                } else {
                    drawerConfig.visible = true
                }
            }).catch((err: any) => {
                drawerConfig.okConfig.loading = false
            })
        } else {
            ElMessage.warning('请将表单输入完整')
        }
    })
}

function closeEvent() {
    drawerConfig.visible = false;
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.app-config-container {
    padding: 12px;

    .config-item {
        .item-title {
            font-size: 12px;
            padding-bottom: 12px;
            box-sizing: border-box;
            border-bottom: 1px solid #ebeef5;
        }

        .el-form {
            padding: 12px 0 0;
            box-sizing: border-box;

            .el-form-item {
                position: relative;

                .tooltip-msg {
                    position: absolute;
                    top: 7px;
                    color: getCssVar('color', 'info');
                    font-size: 16px;
                }

                // 全屏样式
                &.show-screen__full {
                    position: fixed;
                    width: 100%;
                    height: 100%;
                    top: 0;
                    left: 0;
                    background-color: #ffffff;
                    padding: 12px 20px;
                    box-sizing: border-box;
                    transition: all 0.15s linear;
                    z-index: 10;
                    display: flex;
                    flex-direction: column;

                    .el-form-item__content {
                        align-items: flex-start;
                        height: 100%;
                        margin-top: 18px;

                        .modal-full-screen {
                            top: -36px;
                            right: 0;
                            left: unset;
                        }

                        .vue-codemirror {
                            height: calc(100% - 48px);
                        }
                    }
                }

                .el-form-item__label {
                    pointer-events: none;
                }

                .el-form-item__content {
                    .el-radio-group {
                        justify-content: flex-end;
                    }

                    .vue-codemirror {
                        height: 100px;
                        width: 100%;

                        .cm-editor {
                            height: 100%;
                            outline: none;
                            border: 1px solid #dcdfe6;
                        }

                        .cm-gutters {
                            font-size: 12px;
                            font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                        }

                        .cm-content {
                            font-size: 12px;
                            font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                        }

                        .cm-tooltip-autocomplete {

                            // display: none !important;
                            ul {
                                li {
                                    height: 40px;
                                    display: flex;
                                    align-items: center;
                                    font-size: 12px;
                                    background-color: #ffffff;
                                    font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
                                }

                                li[aria-selected] {
                                    background: #409EFF;
                                }

                                .cm-completionIcon {
                                    margin-right: -4px;
                                    opacity: 0;
                                }
                            }
                        }
                    }

                    // 全屏样式
                    .modal-full-screen {
                        position: absolute;
                        top: 8px;
                        left: -22px;
                        cursor: pointer;

                        &:hover {
                            color: getCssVar('color', 'primary');
                            ;
                        }
                    }
                }
            }
        }
    }

    .el-divider {
        margin: 0 0 12px;
    }
}
</style>