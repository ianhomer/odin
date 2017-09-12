import { connect } from 'react-redux'
import { toggleTodo } from '../actions'
import ChannelList from '../components/channelList'

const mapStateToProps = (state) => ({
  channels: state.channels
})

const mapDispatchToProps = {
  onDelete: entity => {
    dispatch(addChannel(entity))
  }
}

const ChannelsContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ChannelList)

export default ChannelsContainer