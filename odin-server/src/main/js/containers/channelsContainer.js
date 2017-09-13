import { connect } from 'react-redux'
import { deleteChannel } from '../actions'
import ChannelList from '../components/channelList'

const mapStateToProps = (state) => ({
  channels: state.channels
})

const mapDispatchToProps = dispatch => {
  return {
    onDeleteChannel: entity=> {
      dispatch(deleteChannel(id))
    }
  }
}

const ChannelsContainer = connect(
  mapStateToProps,
  mapDispatchToProps
)(ChannelList)

export default ChannelsContainer