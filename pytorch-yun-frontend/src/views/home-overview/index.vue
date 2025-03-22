<template>
    <div class="home-overview">
        <div class="ai-top-container">
            <div class="guide-title" v-if="!appInfo">欢迎使用智慧云！</div>
            <div class="app-title" :class="{ 'app-title__show': !!appInfo }">
                <div class="close-chat">
                    <el-button link type="primary" @click="stopChat">退出对话</el-button>
                </div>
                <div>{{ appInfo?.name }}</div>
                <div class="header-option">
                    <el-button link @click="showHistoryEvent">历史记录</el-button>
                </div>
            </div>
        </div>
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
                @keyup="onKeyupEvent"
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
        <AppItem class="ai-app-container" :class="{ 'ai-app-container__hide': isTalking }" @clickAppEvent="clickAppEvent"></AppItem>
        <!-- <HistoryList ref="historyListRef"></HistoryList> -->
    </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref, nextTick } from 'vue'
import ZhyChat from './zhy-chat/index.vue'
import AppItem from './app-item/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import { GetChatDetailData, GetMaxChatData, SendMessageToAi } from '@/services/ai-cheat.service.ts'
import { useAuthStore } from '@/store/useAuth'

// import HistoryList from './history-list/index.vue'

const authStore = useAuthStore()

// 消息部分
const talkMessage = ref<string>('')
const appInfo = ref<any>(null)    // 应用的信息
const chatId = ref<string>('')
const maxChatIndexId = ref<number>(0)

// const historyListRef = ref<any>()
const requestLoading = ref<boolean>(false)  // 发送消息-加载状态
const isTalking = ref<boolean>(false)       // 是否已经开启了对话
const talkMsgList = ref<any[]>([])          // 当前对话的记录

// 打开历史记录
function showHistoryEvent() {
    // historyListRef.value.showModal()
}

// 选择应用
function clickAppEvent(e: any) {
    appInfo.value = e
    isTalking.value = true

    getMaxChatData()
}

// 结束对话
function stopChat() {
    isTalking.value = false
    appInfo.value = null
    talkMessage.value = ''
    maxChatIndexId.value = 0
    chatId.value = ''
    talkMsgList.value = []

    authStore.setChatInfo({
        chatId: chatId.value,
        isTalking: isTalking.value,
        appInfo: null
    })
}

// 获取最大对话
function getMaxChatData() {
    GetMaxChatData({
        chatId: chatId.value || null,
        appId: appInfo.value.id,
    }).then((res: any) => {
        chatId.value = res.data.chatId
        maxChatIndexId.value = res.data.chatIndexId

        authStore.setChatInfo({
            chatId: chatId.value,
            isTalking: isTalking.value,
            appInfo: appInfo.value
        })
    }).catch(() => {

    })
}

// 获取对话结果
function getChatResult() {
    GetChatDetailData({
        chatId: chatId.value || null,
        chatIndex: maxChatIndexId.value
    }).then((res: any) => {
        if (res.data.status === 'CHATTING') {
            setTimeout(() => {
                getChatResult()
            }, 1000);
        } else {
            requestLoading.value = false
            nextTick(() => {
                talkMsgList.value.push({
                    type: 'ai',
                    content: res.data.chatContent.content
                })
            })
            // 会话结束获取最新的索引
            getMaxChatData()
        }
    }).catch(() => {
        requestLoading.value = false
    })
}

// 发送问题
function sendQuestionEvent() {
    isTalking.value = true
    talkMsgList.value.push({
        type: 'user',
        content: talkMessage.value
    })
    requestLoading.value = true
    SendMessageToAi({
        chatId: chatId.value,
        appId: appInfo.value.id,
        maxChatIndexId: maxChatIndexId.value,
        chatContent: {
            content: talkMessage.value,
            // index: talkMsgList.length - 1,
            // role: ''
        }
    }).then((res: any) => {
        talkMessage.value = ''
        maxChatIndexId.value = res.data.responseIndexId
        getChatResult()
    }).catch(() => {
        talkMessage.value = ''
        requestLoading.value = false
    })
}

function onKeyupEvent(e: any) {
    if (e.type === 'keyup' && e.key === 'Enter') {
        sendQuestionEvent()
    }
}

onMounted(() => {
    const chatInfo = authStore.chatInfo
    isTalking.value = chatInfo.isTalking
    appInfo.value = chatInfo.appInfo
    chatId.value = chatInfo.chatId

    if (chatId.value) {
        getMaxChatData()
    }
})
</script>

<style lang="scss">
.home-overview {
    position: relative;
    height: 100vh;
    width: 100%;
    display: flex;
    justify-content: center;
    flex-direction: column;
    .ai-top-container {
        height: calc(100% - 356px);
        overflow-x: hidden;
        overflow-y: auto;
        padding: 50px 60px;
        padding-bottom: 20px;

        .app-title {
            display: flex;
            justify-content: space-between;
            position: absolute;
            width: 100%;
            left: 0;
            top: -50px;
            height: 50px;
            align-items: center;
            border-bottom: 1px solid #0000000f;
            transition: top 0.3s cubic-bezier(0, 0, 0.48, 1.18);
            padding: 0 20px;
            box-sizing: border-box;

            &.app-title__show {
                top: 0;
                transition: top 0.15s 0.3s cubic-bezier(0, 0, 0.48, 1.18);
            }

            .close-chat {
                // position: absolute;
                // top: 0;
                // left: 0;
            }
        }

        .guide-title {
            font-size: 27px;
            color: getCssVar('color', 'primary');
            letter-spacing: 0;
            text-align: center;
            font-weight: 500;
            margin-bottom: 30px;
        }
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

    .ai-app-container {
        visibility: visible;
        transition: visibility 0.3s 0.3s cubic-bezier(0, 0, 0.48, 1.18);
        &.ai-app-container__hide {
            visibility: hidden;
            z-index: 0;
            transition: visibility 0.15s linear;
        }
    }

}
</style>