<template>
    <Breadcrumb :bread-crumb-list="breadCrumbList" />
    <div class="app-detail-options">
        <el-button type="primary" @click="showAppConfigEvent">应用配置</el-button>
    </div>
    <div class="app-detail-preview">
        <ZhyChat
            :isTalking="isTalking"
            :requestLoading="requestLoading"
            :talkMsgList="talkMsgList"
        ></ZhyChat>
        <div class="ai-input-container" :class="{ 'ai-input-container__bottom': isTalking }">
            <el-input
                v-model="talkMessage"
                type="textarea"
                resize="none"
                :readonly="!isTalking"
                placeholder="请输入对话"
                :autosize="{ minRows: 2, maxRows: 2 }"
                @keyup.prevent="onKeyupEvent"
            ></el-input>
            <div class="option-container">
                <el-button v-if="isTalking" link @click="stopChat">新对话</el-button>
                <el-button
                    :disabled="!talkMessage"
                    :loading="requestLoading"
                    @click="sendQuestionEvent"
                >
                    <el-icon><Promotion /></el-icon>
                </el-button>
            </div>
        </div>
    </div>
    <AppConfigModal ref="appConfigModalRef"></AppConfigModal>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, onUnmounted } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import { useRoute, useRouter } from 'vue-router'
import AppConfigModal from '../app-config-modal/index.vue'
import ZhyChat from '../../../home-overview/zhy-chat/index.vue'
import { ElMessage } from 'element-plus'
import { ConfigAppData } from '@/services/app-management.service'


const route = useRoute()
const router = useRouter()

const appConfigModalRef = ref<any>()
// 消息部分
const talkMessage = ref<string>('')
const appId = ref<string>('')
const chatId = ref<string>('')
const isTalking = ref<boolean>(true)
const requestLoading = ref<boolean>(false)  // 发送消息-加载状态
const talkMsgList = ref<any[]>([])          // 当前对话的记录

const breadCrumbList = reactive([
    {
        name: '应用管理',
        code: 'app-management'
    },
    {
        name: '应用预览',
        code: 'app-detail'
    }
])

function initData() {
    if (route.query.id) {
        appId.value = route.query.id
    } else {
        ElMessage.warning('应用不存在')
        router.push({ name: 'app-management' })
    }
}

function onKeyupEvent(e: any) {
    if (e.type === 'keyup' && e.key === 'Enter') {
        sendQuestionEvent()
    }
    // e.stopPropagation();
}

// 发送问题
function sendQuestionEvent() {
    isTalking.value = true
    talkMsgList.value.push({
        type: 'user',
        content: talkMessage.value
    })
    requestLoading.value = true
    talkMessage.value = ''
}

// 应用配置弹窗
function showAppConfigEvent() {
    appConfigModalRef.value.showModal((formData: any) => {
        return new Promise((resolve, reject) => {
            ConfigAppData({
                id: route.query.id,
                ...formData
            }).then((res: any) => {
                ElMessage.success(res.msg)
                resolve()
            }).catch((err: any) => {
                reject(err)
            })
        })
    })
}

onMounted(() => {
    initData()
})
</script>

<style lang="scss">
.app-detail-options {
    position: absolute;
    right: 20px;
    top: 0;
    height: 56px;
    display: flex;
    align-items: center;
}
.app-detail-preview {
    position: relative;
    height: calc(100vh - 56px);
    width: 100%;
    display: flex;
    justify-content: center;
    flex-direction: column;
    .zhy-chat__show {
        top: 20px;
    }
    .ai-input-container {
        width: 80%;
        margin: auto;
        border: 1px solid #0000000f;
        border-radius: 15px;
        padding: 12px 20px 12px 20px;
        box-sizing: border-box;
        transition: bottom 0.15s cubic-bezier(0, 0, 0.48, 1.18);
        position: absolute;
        left: 10%;
        bottom: 65%;
        z-index: 10;
        .el-textarea {
            // height: 80px;
            .el-textarea__inner {
                box-shadow: unset;
            }
        }
        .option-container {
            display: flex;
            justify-content: flex-end;
            margin-top: 12px;
            .el-button {
                border-radius: 20px;
            }
        }

        &.ai-input-container__bottom {
            position: absolute;
            bottom: 20px;
            transition: bottom 0.15s 0.3s cubic-bezier(0, 0, 0.48, 1.18);
        }
    }
}
</style>