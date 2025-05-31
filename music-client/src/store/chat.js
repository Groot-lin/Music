// 聊天状态管理模块
const chat = {
    namespaced: true,
    state: {
        // 聊天消息列表
        messages: []
    },
    mutations: {
        // 添加新消息
        ADD_MESSAGE(state, message) {
            state.messages.push(message)
        },
        // 清空所有消息
        CLEAR_MESSAGES(state) {
            state.messages = []
        }
    },
    actions: {
        // 添加消息的异步操作
        addMessage({ commit }, message) {
            commit('ADD_MESSAGE', message)
        },
        // 清空消息的异步操作
        clearMessages({ commit }) {
            commit('CLEAR_MESSAGES')
        }
    },
    getters: {
        // 获取所有消息
        getAllMessages: state => state.messages,
        // 获取消息数量
        getMessageCount: state => state.messages.length
    }
}

export default chat